package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.MessageRepository;
import com.ade.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final ChatRepository chatRepo;
    private final UserRepository userRepo;

    @Autowired
    public MessageService(MessageRepository messageRepo, ChatRepository chatRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
    }

    public void sendMessage(Long userId, Long chatId, Message msg) {
        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalStateException("No such User with id: " + userId);
        }
        Optional<Chat> chat = chatRepo.findById(chatId);
        if (chat.isEmpty()) {
            throw new IllegalStateException("No such Chat with id: " + chatId);
        }
        if (!chat.get().getMembers().contains(user.get())) {
            throw new IllegalStateException("This user is not a member of a chat");
        }

        msg.setAuthor(user.get());
        msg.setChat(chat.get());
        msg.setDateTime(LocalDateTime.now());
        messageRepo.save(msg);
    }

}
