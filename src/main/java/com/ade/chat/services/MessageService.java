package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис обрабатывающий логику отправки сообщений
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepo;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageMapper messageMapper;
    private final ChatMessagingTemplate messagingTemplate;

    /**
     * Сохраняет сообщение отправленное пользователем в чат
     * @param userId идентификатор отправителя сообщения
     * @param chatId идентификатор чата, в который сообщение было отправлено
     * @param msg само сообщение
     * @return сообщение, которое было сохранено
     * @throws com.ade.chat.exception.UserNotFoundException если неверное айди пользователя
     * @throws com.ade.chat.exception.ChatNotFoundException если неверное айди чата
     * @throws NotAMemberException если пользователь не состоит в чате
     */
    public Message sendMessage(Long userId, Long chatId, Message msg) {
        User user = userService.getUserByIdOrException(userId);
        Chat chat = chatService.getChatByIdOrException(chatId);

        if (!chat.getMembers().contains(user)) {
            throw new NotAMemberException("This user: " + userId + " is not a member of a chat: " + chatId);
        }
        Message sent = sendToChatFromUser(user, chat, msg);
        chatService.updateLastMessage(chat, sent);
        chatService.changeUnreadCounter(chat, user);
        messagingTemplate.sendMessageNotificationsToMembers(sent, chat);
        return sent;
    }

    private Message sendToChatFromUser(User user, Chat chat, Message msg) {
        msg.setAuthor(user);
        if (msg.getDateTime() == null)  {
            msg.setDateTime(LocalDateTime.now());
        }
        msg.setChat(chat);
        return messageRepo.save(msg);
    }
}
