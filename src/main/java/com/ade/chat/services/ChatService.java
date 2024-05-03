package com.ade.chat.services;

import com.ade.chat.domain.*;
import com.ade.chat.dtos.ChangeGroupChatInfoRequest;
import com.ade.chat.dtos.ReadNotification;
import com.ade.chat.exception.AbsentGroupInfoException;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.IllegalMemberCount;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.MessageRepository;
import com.ade.chat.repositories.UnreadCounterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис, обрабатывающий запросы и реализующий логику работы с чатами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepo;
    private final UserService userService;
    private final ChatMessagingTemplate messagingTemplate;
    private final UnreadCounterRepository counterRepository;
    private final MinioService minioService;

    /**
     * @param id идентификатор чата
     * @return найденный чат по идентификатору
     * @throws ChatNotFoundException если чат не был найден
     */
    public Chat getChatByIdOrException(Long id) {
        return chatRepo.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("No chat with such id: " + id));
    }

    /**
     * Проверяет наличие чата, возвращает его или создает новый в случае отсутствия
     * @param id1 идентификатор первого пользователя
     * @param id2 идентификатор второго пользователя
     * @return диалог
     * @throws com.ade.chat.exception.UserNotFoundException если переданы неверные идентификаторы
     */
    public Chat createOrGetPrivateChat(Long id1, Long id2) {
        Optional<Chat> existing = privateChatBetweenUsersByIds(id1, id2);
        return existing.orElseGet(() -> createPrivateChat(id1, id2));
    }

    /**
     * Создает новый групповой чат между произвольным числом пользователей
     * устанавливает дату создания
     * @param ids список идентификаторов участников
     * @param groupInfo содержит дополнительную информацию о беседе
     * @return созданный чат
     */
    public Chat createGroupChat(List<Long> ids, Group groupInfo) {
        if (groupInfo == null) {
            throw new AbsentGroupInfoException("group chat creation require \"groupInfo\"");
        }
        if (!ids.contains(groupInfo.getCreator().getId())) {
            throw new NotAMemberException("Creator is not a member of a group chat");
        }

        Chat chat = Chat.builder().isPrivate(false).group(groupInfo).build();
        ids.forEach(id -> addMemberById(chat, id));

        groupInfo.setCreationDate(LocalDate.now());
        groupInfo.setChat(chat);

        Chat saved = chatRepo.save(chat);
        saved.getMembers().forEach(member -> addUnreadCounter(saved, member));

        log.info("group chat created: members={}", saved.getMembers().stream().map(User::getId).toList());
        messagingTemplate.sendCreationNotificationToMembers(saved);
        return saved;
    }

    private void addUnreadCounter(Chat chat, User member) {
        var counter = UnreadCounter.builder().chat(chat).user(member).build();
        var saved = counterRepository.save(counter);
        chat.getMemberUnreadCounters().add(saved);
    }


    /**
     * @param chatId идентификатор чата
     * @return список сообщений из чата сортированный по времени сообщений.
     */
    public List<Message> getMessages(Long chatId) {
        Chat chat = getChatByIdOrException(chatId);
        return chat.getMessages().stream()
                .sorted(Comparator.comparing(Message::getDateTime))
                .toList();
    }

    private Chat createPrivateChat(Long id1, Long id2) {
        Chat chat = Chat.builder()
                .isPrivate(true)
                .build();
        addMemberById(chat, id1);
        addMemberById(chat, id2);
        Chat saved = chatRepo.save(chat);
        addUnreadCounter(saved, userService.getUserByIdOrException(id1));
        addUnreadCounter(saved, userService.getUserByIdOrException(id2));

        log.info("private chat created: members={}", saved.getMembers().stream().map(User::getId).toList());
        messagingTemplate.sendCreationNotificationToMembers(saved);
        return saved;
    }

    private void addMemberById(Chat chat, Long id) {
        chat.getMembers().add(userService.getUserByIdOrException(id));
    }

    private Optional<Chat> privateChatBetweenUsersByIds(Long id1, Long id2) {
        Set<Chat> intersection = chatRepo.findByMembers_IdAndIsPrivateTrue(id1);
        Set<Chat> other = chatRepo.findByMembers_IdAndIsPrivateTrue(id2);
        intersection.retainAll(other);
        return intersection.stream().findAny();
    }

    /**
     * @param chat чат, в котором нужно обновить последнее сообщение
     * @param message новое последнее сообщение
     */
    public void updateLastMessage(Chat chat, Message message) {
        if (chat.getLastMessageTime().isBefore(message.getDateTime())) {
            chatRepo.updateLastMessageById(message, chat.getId());
        }
    }

    /**
     * @param chat чат, в котором нужно поменять количество непрочитанных сообщений
     * @param user член чата, для которого считаются сообщения
     */
    public void changeUnreadCounter(Chat chat, User user) {
        for (var member : chat.getMembers()) {
            if (user != member) {
                counterRepository.incrementCountByChatAndUser(chat, member);
            }
        }
    }

    /**
     * Сбрасывает счетчик непрочитанных для прочитавшего пользователя
     * и пересылает уведомление о прочтении всем членам чата
     * @param notification уведомление о прочтении от пользователя
     */
    public void processReadNotification(ReadNotification notification) {
        Chat chat = getChatByIdOrException(notification.getChatId());
        User user = userService.getUserByIdOrException(notification.getUserId());
        counterRepository.setCountToZeroByChatAndUser(chat, user);
        messagingTemplate.sendReadNotificationToMembers(chat, notification);
    }

    /**
     * Добавляет нового члена в чат, добавляет вспомогательное сообщение о добавлении
     * @param chatId идентификатор чата, чат должен быть групповым
     * @param memberId идентификатор добавляемого пользователя
     * @param authValue токен авторизации
     * @return измененный чат
     */
    public Chat addNewMember(Long chatId, Long memberId, String authValue) {
        Chat chat = getChatByIdOrException(chatId);
        User newMember = userService.getUserByIdOrException(memberId);
        User invitor = userService.getUserFromToken(authValue);

        if (chat.getIsPrivate()) {
            throw new IllegalMemberCount("Cannot add new member to a private chat");
        }
        if (!chat.getMembers().contains(invitor)) {
            throw new NotAMemberException("Invitor should be a member of a chat");
        }

        chat.getMembers().add(newMember);

        Message addMemberMessage = createAuxilaryMessage(
                String.format("%s added %s to the Chat", invitor.getUsername(), newMember.getUsername()),
                chat
        );
        chat.getMessages().add(addMemberMessage);

        messagingTemplate.sendToUserChatQueue(newMember.getId(), chat);
        messagingTemplate.sendMessageNotificationsToMembers(addMemberMessage, chat);
        return chatRepo.save(chat);
    }


    /**
     * Удаляет чат, сделать это может только создатель чата
     * @param chatId идентификатор чата
     * @param authValue токен авторизации
     * @throws NotAMemberException при попытке удаления не создателем
     * @return измененный чат
     */
    public Chat deleteChatById(Long chatId, String authValue) {
        Chat chat = getChatByIdOrException(chatId);
        User deleter = userService.getUserFromToken(authValue);
        if (chat.getIsPrivate()) {
            throw new AbsentGroupInfoException("Cannot delete private Chat");
        }
        if (chat.getGroup().getCreator() != deleter) {
            throw new NotAMemberException("Only creator can delete the chat");
        }
        messagingTemplate.sendDeleteNotificationToMembers(chat);
        chatRepo.delete(chat);
        return chat;
    }

    private Message createAuxilaryMessage(String text, Chat chat) {
        return messageRepository.save(
                Message.builder()
                .isAuxiliary(true)
                .text(text)
                .chat(chat)
                .dateTime(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Удаляет члена из чата, при этом он теряет историю сообщений,
     * может сделать это только создатель чата, создает вспомогательное сообщение об удалении,
     * посылает всем членам чата уведомление
     * @param chatId идентификатор чата
     * @param memberId идентификатор удаляемого
     * @param authValue токен авторизации
     * @return измененный чат
     */
    public Chat deleteMember(Long chatId, Long memberId, String authValue) {
        Chat chat = getChatByIdOrException(chatId);
        User member = userService.getUserByIdOrException(memberId);
        User deleter = userService.getUserFromToken(authValue);

        if (chat.getIsPrivate()) {
            throw new IllegalMemberCount("Cannot delete member from a private chat");
        }
        if (!Objects.equals(memberId, deleter.getId()) && chat.getGroup().getCreator() != deleter) {
            throw new NotAMemberException("user can leave, but only creator can delete members from chat");
        }

        Message deletedMemberAuxiliaryMessage;
        if (Objects.equals(memberId, deleter.getId())) {
             deletedMemberAuxiliaryMessage = createAuxilaryMessage(
                    String.format("%s left the Chat", member.getUsername()),
                    chat
            );
        }
        else {
            deletedMemberAuxiliaryMessage = createAuxilaryMessage(
                    String.format("%s added %s to the Chat", deleter.getUsername(), member.getUsername()),
                    chat
            );
        }

        chat.getMessages().add(deletedMemberAuxiliaryMessage);
        messagingTemplate.sendMessageNotificationsToMembers(deletedMemberAuxiliaryMessage, chat);
        messagingTemplate.sendToUserChatDeleteQueue(member.getId(), chat);

        chat.getMembers().remove(member);
        return chatRepo.save(chat);
    }

    /**
     * Изменяет аватарку группового чата
     * @param chatId идентификатор чата
     * @param file файл с новой аватаркой, должен быть изображением.
     * @return измененный объект чата
     */
    public Chat uploadGroupPhoto(Long chatId, MultipartFile file) {
        Chat chat = getChatByIdOrException(chatId);
        if (chat.getIsPrivate() || chat.getGroup() == null) {
            throw new UnsupportedOperationException("cannot upload photo for private chat");
        }

        String photoId = minioService.uploadFile(file);
        chat.getGroup().setGroupPhotoId(photoId);

        return chatRepo.save(chat);
    }


    /**
     * Изменяет название группового чата
     * @param chatId идентификатор чата
     * @param changeRequest объект, который содержит новый значения
     * @return измененный чат
     */
    public Chat changeGroupInfo(Long chatId, ChangeGroupChatInfoRequest changeRequest) {
        Chat chat = getChatByIdOrException(chatId);
        if (chat.getIsPrivate() || chat.getGroup() == null) {
            throw new UnsupportedOperationException("cannot upload photo for private chat");
        }
        if (!changeRequest.getGroupName().isBlank() && !Objects.equals(chat.getGroup().getName(), changeRequest.getGroupName())) {
           chat.getGroup().setName(changeRequest.getGroupName());
        }
        return chatRepo.save(chat);
    }
}
