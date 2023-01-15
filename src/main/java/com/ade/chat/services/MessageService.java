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

    private void sendToChatFromUser(User user, Chat chat, Message msg) {
        msg.setAuthor(user);
        msg.setChat(chat);
        msg.setDateTime(LocalDateTime.now());
        messageRepo.save(msg);
    }

    public void sendMessage(Long userId, Long chatId, Message msg) {
        User user = userService.getUserByIdOrException(userId);
        Chat chat = chatService.getChatByIdOrException(chatId);

        if (!chat.getMembers().contains(user)) {
            throw new IllegalStateException(
                    "This user: " + userId + " is not a member of a chat: " + chatId
            );
        }
        sendToChatFromUser(user, chat, msg);
    }

    public void sendPrivateMessage(Long fromUserId, Long toUserId, Message msg) {
        List<Long> ids = List.of(fromUserId, toUserId);
        User fromUser = userService.getUserByIdOrException(fromUserId);

        Optional<Chat> privateChat = chatService.getPrivateChatByMemberIds(ids);
        if (privateChat.isPresent()) {
            sendToChatFromUser(fromUser, privateChat.get(), msg);
            return;
        }

        var chat =  chatService.createChat(ids, true);
        sendToChatFromUser(fromUser, chat, msg);
    }
}
