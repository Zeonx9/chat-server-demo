package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

    private final ChatRepository chatRepo;
    private final UserService userService;

    @Autowired
    public ChatService(ChatRepository chatRepo, UserService userService) {
        this.chatRepo = chatRepo;
        this.userService = userService;
    }

    public Chat getChatByIdOrException(Long id) {
        return chatRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("No chat with such id: " + id));
    }

    public Optional<Chat> privateChatBetweenUsers(User first, User second) {
        Set<Chat> intersect = new HashSet<>(first.getChats());
        intersect.retainAll(second.getChats());
        for (Chat chat : intersect) {
            if (chat.getIsPrivate())
                return Optional.of(chat);
        }
        return Optional.empty();
    }

    public Optional<Chat> privateChatBetweenUsersWithIdsExists(List<Long> ids) {
        if (ids.size() != 2)
            throw new IllegalArgumentException("size of id list for private chat must equals 2");
        User u1 = userService.getUserByIdOrException(ids.get(0));
        User u2 = userService.getUserByIdOrException(ids.get(1));
        return privateChatBetweenUsers(u1, u2);
    }


    public Chat createChat(List<Long> ids, Boolean isPrivate) {
        if (isPrivate == null)
            isPrivate = false;

        if (isPrivate) {
            // private chat between two people should be the only
            if (privateChatBetweenUsersWithIdsExists(ids).isPresent()) {
                throw new IllegalStateException("This chat already exists");
            }
        }

        Chat chat = new Chat(isPrivate, new HashSet<>());
        ids.forEach(id ->
                chat.addMember(userService.getUserByIdOrException(id))
        );

        return chatRepo.save(chat);
    }

    public List<Message> getMessages(Long chatId) {
        return getChatByIdOrException(chatId).getMessages();
    }
}
