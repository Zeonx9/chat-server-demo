package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepo;
    private final UserService userService;

    /**
     * @param id идентификатор чата
     * @return найденный чат по идентификатору
     * @throws IllegalStateException если чат не был найден
     */
    public Chat getChatByIdOrException(Long id) {
        return chatRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("No chat with such id: " + id));
    }

    /**
     * @param first первый пользователь
     * @param second второй пользователь
     * @return найденный общий приватный чат между переданными пользователями или Optional.empty()
     */
    public Optional<Chat> privateChatBetweenUsers(User first, User second) {
        Set<Chat> intersect = new HashSet<>(first.getChats());
        intersect.retainAll(second.getChats());
        for (Chat chat : intersect) {
            if (chat.getIsPrivate())
                return Optional.of(chat);
        }
        return Optional.empty();
    }

    /**
     * ищет пользователей по ID, затем их личный диалог
     * @param ids список идентификаторов пользователей (ровно 2 ID)
     * @return найденный общий приватный чат между переданными пользователями или Optional.empty()
     * @throws IllegalArgumentException если размер списка ID не равен 2
     */
    public Optional<Chat> privateChatBetweenUsersWithIds(List<Long> ids) {
        if (ids.size() != 2)
            throw new IllegalArgumentException("size of id list for private chat must equals 2");
        User u1 = userService.getUserByIdOrException(ids.get(0));
        User u2 = userService.getUserByIdOrException(ids.get(1));
        return privateChatBetweenUsers(u1, u2);
    }

    /**
     * Создает чат между пользователями. приватный диалог может быть создан лишь однажды
     * @param ids список идентификаторов пользователей (ровно 2 ID)
     * @param isPrivate true для приватных диалогов false для бесед
     * @return созданный чат
     * @throws IllegalStateException при попытке создать существующий личный диалог
     */
    public Chat createChat(List<Long> ids, Boolean isPrivate) {
        if (isPrivate == null)
            isPrivate = false;

        if (isPrivate && privateChatBetweenUsersWithIds(ids).isPresent()) {
            throw new IllegalStateException("This chat already exists");
        }

        Chat chat = new Chat(isPrivate, new HashSet<>());
        ids.forEach(id ->
                chat.getMembers().add(userService.getUserByIdOrException(id))
        );

        return chatRepo.save(chat);
    }

    /**
     * @param chatId идентификатор чата, из которого запрошены сообщения
     * @return список сообщений из соответствующего чата
     */
    public List<Message> getMessages(Long chatId) {
        return getChatByIdOrException(chatId).getMessages();
    }
}
