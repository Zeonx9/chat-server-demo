package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.dtos.ReadNotification;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagingTemplate {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;
    private final ChatMapper chatMapper;

    private void sendToUser(Long id, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(id.toString(), destination, payload);
    }

    public void sendToUserMessageQueue(Long userId, MessageDto message) {
        sendToUser(userId, "/queue/messages", message);
    }

    public void sendToUserChatQueue(Long userId, ChatDto chat) {
        sendToUser(userId, "/queue/chats", chat);
    }

    public void sendToUserNotificationQueue(Long userId, ReadNotification notification) {
        sendToUser(userId, "/queue/read_notifications", notification);
    }

    public void sendToUserChatDeleteQueue(Long userId, ChatDto chat) {
        sendToUser(userId, "/queue/chats_delete", chat);
    }

    public void sendToUserChatQueue(Long userId, Chat chat) {
        sendToUserChatQueue(userId, chatMapper.toDto(chat));
    }

    public void sendToUserChatDeleteQueue(Long userId, Chat chat) {
        sendToUserChatDeleteQueue(userId, chatMapper.toDto(chat));
    }

    public void sendCreationNotificationToMembers(Chat chat) {
        ChatDto chatDto = chatMapper.toDto(chat);
        for (var member : chatDto.getMembers()) {
            sendToUserChatQueue(member.getId(), chatDto);
        }
    }

    public void sendReadNotificationToMembers(Chat chat, ReadNotification notification) {
        for (var member : chat.getMembers()) {
            sendToUserNotificationQueue(member.getId(), notification);
        }
    }

    public void sendDeleteNotificationToMembers(Chat chat) {
        ChatDto chatDto = chatMapper.toDto(chat);
        for (var member : chat.getMembers()) {
            sendToUserChatDeleteQueue(member.getId(), chatDto);
        }
    }

    public void sendMessageNotificationsToMembers(Message message, Chat chat) {
        MessageDto sentAsDto = messageMapper.toDto(message);
        for (var member: chat.getMembers()) {
            sendToUserMessageQueue(member.getId(), sentAsDto);
        }
    }
}
