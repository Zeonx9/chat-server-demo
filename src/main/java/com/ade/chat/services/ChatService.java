package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepo;
    private final UserRepository userRepo;

    @Autowired
    public ChatService(ChatRepository chatRepo, UserRepository userRepo) {
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
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

        Chat chat = new Chat(isPrivate, new HashSet<User>());
        for (var id : ids) {
            Optional<User> member = userRepo.findById(id);
            if (member.isEmpty()) {
                throw new IllegalStateException("Cannot add to the chat, no such user");
            }
            chat.getMembers().add(member.get());
        }
        chatRepo.save(chat);
    }

    public List<Message> getMessages(Long chatId) {
        Optional<Chat> chat = chatRepo.findById(chatId);
        if (chat.isEmpty()) {
            throw new IllegalStateException("No such chat with id: " + chatId);
        }
        return chat.get().getMessages();
    }
}
