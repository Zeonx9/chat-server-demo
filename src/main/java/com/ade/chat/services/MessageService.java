package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepo;
    private final ChatService chatService;
    private final UserService userService;

    /**
     * сохраняет сообщение отправленное пользователем в чат
     * @param user отправитель сообщения
     * @param chat чат, в который сообщение было отправлено
     * @param msg само сообщение
     * @return сообщение, которое было сохранено
     */
    private Message sendToChatFromUser(User user, Chat chat, Message msg) {
        var otherMembers = new LinkedHashSet<>(chat.getMembers());
        otherMembers.remove(user);

        msg.setAuthor(user);
        msg.setChat(chat);
        msg.setDateTime(LocalDateTime.now());
        msg.setUndeliveredTo(otherMembers);
        return messageRepo.save(msg);
    }

    /**
     * сохраняет сообщение отправленное пользователем в чат
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
            throw new NotAMemberException(
                    "This user: " + userId + " is not a member of a chat: " + chatId
            );
        }
        return sendToChatFromUser(user, chat, msg);
    }

    /**
     * сохраняет личное сообщение от одного пользователя другому, если между ними не было диалога - он созадется
     * @param fromUserId идентификатор отправителя сообщения
     * @param toUserId идентификатор получателя сообщения
     * @param msg само сообщение
     * @throws com.ade.chat.exception.UserNotFoundException если неверное айди пользователя
     */
    public Message sendPrivateMessage(Long fromUserId, Long toUserId, Message msg) {
        User fromUser = userService.getUserByIdOrException(fromUserId);
        var chat =  chatService.createOrGetChat(List.of(fromUserId, toUserId), true);
        return sendToChatFromUser(fromUser, chat, msg);
    }
}
