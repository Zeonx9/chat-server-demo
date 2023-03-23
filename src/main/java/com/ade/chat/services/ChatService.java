package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Group;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.IllegalMemberCount;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Optional<Chat> existing = privateChatBetweenUsersByIds(id1, id2);
        return existing.orElse(createChat(List.of(id1, id2), true, null));
    }

    /**
     * создает новый групповой чат между произвольным числом пользователей
     * устанавливает дату создания
     * @param ids список идентификаторов участников
     * @param groupInfo содержит дополнительную информацию о беседе
     * @return созданный чат
     */
    public Chat createGroupChat(List<Long> ids, Group groupInfo) {
        return createChat(ids, false, groupInfo);
    }

    /**
     * получает сообщения из чата для, и помечает их, как доставленные
     * @param chatId идентификатор чата, из которого запрошены сообщения
     * @return список сообщений из соответствующего чата
     * @throws ChatNotFoundException если дан неверный ID чата
     */
    @Transactional
    public List<Message> getMessages(Long chatId, Long userId) {
        Chat chat = getChatByIdOrException(chatId);
        if (userId != null) {
            User user = userService.getUserByIdOrException(userId);
            markAsDeliveredAllFor(chat, user);
        }
        return chat.getMessages();
    }

    private Chat createChat(List<Long> ids, Boolean isPrivate, Group groupInfo) {
        if (groupInfo != null) {
            groupInfo.setCreationDate(LocalDate.now());
            if (!ids.contains(groupInfo.getCreator().getId())) {
                throw new NotAMemberException("Creator is not a member of a group chat");
            }
        }

        Chat chat = Chat.builder()
                .isPrivate(isPrivate)
                .group(groupInfo)
                .build();

        ids.forEach(id -> addMemberById(chat, id));
        return chatRepo.save(chat);
    }

    private void addMemberById(Chat chat, Long id) {
        chat.getMembers().add(userService.getUserByIdOrException(id));
    }

    private Optional<Chat> privateChatBetweenUsersByIds(Long id1, Long id2) {
        User u1 = userService.getUserByIdOrException(id1);
        User u2 = userService.getUserByIdOrException(id2);
        return privateChatBetweenUsers(u1, u2);
    }

    private Optional<Chat> privateChatBetweenUsers(User first, User second) {
        Set<Chat> intersect = new HashSet<>(first.getChats());
        intersect.retainAll(second.getChats());
        return intersect.stream()
                .filter(Chat::getIsPrivate)
                .findAny();
    }

    private void markAsDeliveredAllFor(Chat chat, User user) {
        Set<Message> undelivered = new LinkedHashSet<>(user.getUndeliveredMessages());
        undelivered.retainAll(chat.getMessages());
        undelivered.forEach(message -> message.removeRecipient(user));
    }

    /**
     * Создает чат между пользователями. Или возвращает уже существующий личный диалог
     * @param ids список идентификаторов пользователей (ровно 2 ID)
     * @param isPrivate true для приватных диалогов false для бесед
     * @return созданный чат
     */
    @Deprecated
    public Chat createOrGetChat(List<Long> ids, boolean isPrivate) {
        if (isPrivate){
            Optional<Chat> possiblePreviousPrivateChat = privateChatBetweenUsersWithIds(ids);
            if (possiblePreviousPrivateChat.isPresent()) {
                return possiblePreviousPrivateChat.get();
            }
        }
        return createChat(ids, isPrivate, null);
    }

    /**
     * ищет пользователей по ID, затем их личный диалог
     * @param ids список идентификаторов пользователей (ровно 2 ID)
     * @return найденный общий приватный чат между переданными пользователями или Optional.empty()
     * @throws IllegalMemberCount если размер списка ID не равен 2
     * @throws com.ade.chat.exception.UserNotFoundException если в списке неверные ID
     */
    public Optional<Chat> privateChatBetweenUsersWithIds(List<Long> ids) {
        if (ids.size() != 2)
            throw new IllegalMemberCount("size of id list for private chat must equals 2");
        return privateChatBetweenUsersByIds(ids.get(0), ids.get(1));
    }
}
