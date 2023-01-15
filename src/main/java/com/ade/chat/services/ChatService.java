package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.repositories.ChatRepository;
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
        return chatRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("No chat with such id: " + id));
    }

    public Optional<Chat> getPrivateChatByMemberIds(List<Long> ids) {
        if (ids.size() != 2)
            throw new IllegalStateException("private chat is only for 2 users, not for " + ids.size());
        return chatRepo.findPrivateByMemberIds(ids);
    }

    public Chat createChat(List<Long> ids, Boolean isPrivate) {
        if (isPrivate) {
            // private chat between two people should be the only
            Optional<Chat> privateChat = getPrivateChatByMemberIds(ids);
            if (privateChat.isPresent()) {
                throw new IllegalStateException("This chat already exists");
            }
        }

        Chat chat = new Chat(isPrivate, new HashSet<>());
        ids.forEach((id) ->
                chat.getMembers().add(userService.getUserByIdOrException(id))
        );
        chatRepo.save(chat);
        return chat;
    }

    public List<Message> getMessages(Long chatId) {
        return getChatByIdOrException(chatId).getMessages();
    }
}
