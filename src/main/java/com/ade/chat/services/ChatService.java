package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Group;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.AbsentGroupInfoException;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepo;
    private final UserService userService;

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
     * проверяет наличие чата, возвращает его или создает новый в случае отсутствия
     * @param id1 идентификатор первого пользователя
     * @param id2 идентификатор второго пользователя
     * @return диалог
     * @throws com.ade.chat.exception.UserNotFoundException если переданы неверные идентификаторы
     */
    public Chat createOrGetPrivateChat(Long id1, Long id2) {
        System.out.print("chat request: " + id1 + " and " + id2 + " ");
        Optional<Chat> existing = privateChatBetweenUsersByIds(id1, id2);
        System.out.println(existing.isPresent() ? "existed" : "created");
        return existing.orElseGet(() -> createPrivateChat(id1, id2));
    }

    /**
     * создает новый групповой чат между произвольным числом пользователей
     * устанавливает дату создания
     * @param ids список идентификаторов участников
     * @param groupInfo содержит дополнительную информацию о беседе
     * @return созданный чат
     */
    public Chat createGroupChat(List<Long> ids, Group groupInfo) {
        System.out.println("group creation request");

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

        return chatRepo.save(chat);
    }


    public List<Message> getMessages(Long chatId, Long userId) {
        Chat chat = getChatByIdOrException(chatId);
        if (userId != null) {
            User user = userService.getUserByIdOrException(userId);
            markAsDeliveredAllFor(chat, user);
        }
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
        return chatRepo.save(chat);
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

    private void markAsDeliveredAllFor(Chat chat, User user) {
        Set<Message> undelivered = new LinkedHashSet<>(user.getUndeliveredMessages());
        undelivered.retainAll(chat.getMessages());
        undelivered.forEach(message -> message.removeRecipient(user));
    }

    public void updateLastMessage(Long chatId, Message message) {
        chatRepo.updateLastMessageById(message, chatId);
    }
}
