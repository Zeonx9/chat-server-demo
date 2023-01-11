package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
        Optional<Chat> chatOptional = chatRepo.findById(id);
        if (chatOptional.isEmpty()) {
            throw new IllegalStateException("No chat with such id: " + id);
        }
        return chatOptional.get();
    }

    public Optional<Chat> getPrivateChatByMembers(List<Long> ids) {
        if (ids.size() != 2)
            throw new IllegalStateException("Private chat is only between 2 users, " +
                    "the method should not be used to search public chats");
        return chatRepo.findPrivateByMembers(ids);
    }

    public void createChat(List<Long> ids, Boolean isPrivate) {
        if (isPrivate) {
            if (ids.size() != 2) {
                throw new IllegalStateException(
                        "dialog (private chat) must have only 2 members, but: " + ids.size() + " were provided"
                );
            }
            Optional<Chat> privateChatByMembers = chatRepo.findPrivateByMembers(ids);
            if (privateChatByMembers.isPresent()) {
                throw new IllegalStateException("This chat already exists");
            }
        }

        Chat chat = new Chat(isPrivate, new HashSet<>());
        for (var id : ids) {
            User member = userService.getUserByIdOrException(id);
            chat.getMembers().add(member);
        }
        chatRepo.save(chat);
    }

    public List<Message> getMessages(Long chatId) {
        return getChatByIdOrException(chatId).getMessages();
    }
}
