package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.IllegalMemberCount;
import com.ade.chat.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param first первый пользователь
     * @param second второй пользователь
     * @return найденный общий приватный чат между переданными пользователями или Optional.empty()
     */
    public Optional<Chat> privateChatBetweenUsers(User first, User second) {
        Set<Chat> intersect = new HashSet<>(first.getChats());
        intersect.retainAll(second.getChats());
        return intersect.stream()
                .filter(Chat::getIsPrivate)
                .findAny();
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
        User u1 = userService.getUserByIdOrException(ids.get(0));
        User u2 = userService.getUserByIdOrException(ids.get(1));
        return privateChatBetweenUsers(u1, u2);
    }

    /**
     * Создает чат между пользователями. Или возвращает уже существующий личный диалог
     * @param ids список идентификаторов пользователей (ровно 2 ID)
     * @param isPrivate true для приватных диалогов false для бесед
     * @return созданный чат
     */
    public Chat createOrGetChat(List<Long> ids, Boolean isPrivate) {
        if (isPrivate == null) {
            isPrivate = false;
        }

        if (isPrivate){
            Optional<Chat> possiblePreviousPrivateChat = privateChatBetweenUsersWithIds(ids);
            if (possiblePreviousPrivateChat.isPresent()) {
                return possiblePreviousPrivateChat.get();
            }
        }

        return createChat(ids, isPrivate);
    }

    private Chat createChat(List<Long> ids, Boolean isPrivate) {
        Chat chat = Chat.builder()
                .isPrivate(isPrivate)
                .build();

        ids.forEach(id -> addMemberById(chat, id));
        return chatRepo.save(chat);
    }

    private void addMemberById(Chat chat, Long id) {
        chat.getMembers().add(userService.getUserByIdOrException(id));
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
        if (userId == null) {
            return chat.getMessages();
        }

        User user = userService.getUserByIdOrException(userId);
        Set<Message> undelivered = new LinkedHashSet<>(user.getUndeliveredMessages());
        undelivered.retainAll(chat.getMessages());
        undelivered.forEach(message -> message.removeRecipient(user));
        return chat.getMessages();
    }
}
