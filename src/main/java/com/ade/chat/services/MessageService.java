package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
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

    /**
     * сохраняет сообщение отправленное пользователем в чат
     * @param user отправитель сообщения
     * @param chat чат, в который сообщение было отправлено
     * @param msg само сообщение
     */
    private void sendToChatFromUser(User user, Chat chat, Message msg) {
        msg.setAuthor(user);
        msg.setChat(chat);
        msg.setDateTime(LocalDateTime.now());
        messageRepo.save(msg);
    }

    /**
     * сохраняет сообщение отправленное пользователем в чат
     * @param userId идентификатор отправителя сообщения
     * @param chatId идентификатор чата, в который сообщение было отправлено
     * @param msg само сообщение
     */
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

    /**
     * сохраняет личное сообщение от одного пользователя другому, если между ними не было диалога - он созадется
     * @param fromUserId идентификатор отправителя сообщения
     * @param toUserId идентификатор получателя сообщения
     * @param msg само сообщение
     */
    public void sendPrivateMessage(Long fromUserId, Long toUserId, Message msg) {
        User fromUser = userService.getUserByIdOrException(fromUserId);
        User toUser = userService.getUserByIdOrException(toUserId);

        Optional<Chat> privateChat = chatService.privateChatBetweenUsers(fromUser, toUser);
        if (privateChat.isPresent()) {
            sendToChatFromUser(fromUser, privateChat.get(), msg);
            return;
        }

        var chat =  chatService.createChat(List.of(fromUserId, toUserId), true);
        sendToChatFromUser(fromUser, chat, msg);
    }
}
