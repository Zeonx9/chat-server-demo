package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepo, ChatService chatService, UserService userService) {
        this.messageRepo = messageRepo;
        this.chatService = chatService;
        this.userService = userService;
    }

    public void sendMessage(Long userId, Long chatId, Message msg) {
        User user = userService.getUserByIdOrException(userId);
        Chat chat = chatService.getChatByIdOrException(chatId);

        if (!chat.getMembers().contains(user)) {
            throw new IllegalStateException("This user is not a member of a chat");
        }

        msg.setAuthor(user);
        msg.setChat(chat);
        msg.setDateTime(LocalDateTime.now());
        messageRepo.save(msg);
    }

    public void sendPrivateMessage(Long fromUserId, Long toUserId, Message msg) {
        List<Long> ids = List.of(fromUserId, toUserId);
        Optional<Chat> privateChat = chatService.getPrivateChatByMemberIds(ids);

        if (privateChat.isPresent()) {
            sendMessage(fromUserId, privateChat.get().getId(), msg);
        } else {
            var chat =  chatService.createChat(ids, true);
            sendMessage(fromUserId, chat.getId(), msg);
        }
    }
}
