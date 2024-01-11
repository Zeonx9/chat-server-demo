package com.ade.chat.services;

import com.ade.chat.domain.*;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.ReadNotification;
import com.ade.chat.exception.AbsentGroupInfoException;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.UnreadCounterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис, обрабатывающий запросы и реализующий логику работы с чатами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepo;
    private final UserService userService;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UnreadCounterRepository counterRepository;

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
        sendCreationNotificationToMembers(saved);
        return saved;
    }

    private void addUnreadCounter(Chat chat, User member) {
        var counter = UnreadCounter.builder().chat(chat).user(member).build();
        var saved = counterRepository.save(counter);
        chat.getMemberUnreadCounters().add(saved);
    }


    public List<Message> getMessages(Long chatId, Long userId) {
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
        sendCreationNotificationToMembers(saved);
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

    public void updateLastMessage(Chat chat, Message message) {
        if (chat.getLastMessageTime().isBefore(message.getDateTime())) {
            chatRepo.updateLastMessageById(message, chat.getId());
        }
    }

    private void sendCreationNotificationToMembers(Chat chat) {
        ChatDto chatDto = chatMapper.toDto(chat);
        for (var member : chatDto.getMembers()) {
            messagingTemplate.convertAndSendToUser(member.getId().toString(), "/queue/chats", chatDto);
        }
    }

    public void changeUnreadCounter(Chat chat, User user) {
        for (var member : chat.getMembers()) {
            if (user != member) {
                counterRepository.incrementCountByChatAndUser(chat, member);
            }
        }
    }

    private void sendReadNotificationToChatMembers(Chat chat, ReadNotification notification) {
        for (var member : chat.getMembers()) {
            messagingTemplate.convertAndSendToUser(member.getId().toString(), "/queue/read_notifications", notification);
        }
    }

    public void processReadNotification(ReadNotification notification) {
        Chat chat = getChatByIdOrException(notification.getChatId());
        User user = userService.getUserByIdOrException(notification.getUserId());
        counterRepository.setCountToZeroByChatAndUser(chat, user);
        sendReadNotificationToChatMembers(chat, notification);
    }
}
