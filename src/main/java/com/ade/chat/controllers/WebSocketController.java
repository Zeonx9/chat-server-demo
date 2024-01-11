package com.ade.chat.controllers;

import com.ade.chat.dtos.ConnectEvent;
import com.ade.chat.dtos.ReadNotification;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.services.ChatService;
import com.ade.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    private final UserService userService;
    private final ChatService chatService;

    @MessageMapping("/connect")
    @SendTo("/topic/connection")
    @Transactional
    public ConnectEvent onUserConnected(@Payload UserDto user) {
        log.info("user id={} connected", user.getId());
        userService.setOnline(user.getId());
        return ConnectEvent.builder().userId(user.getId()).connect(true).build();
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/connection")
    public ConnectEvent onUserDisconnected(@Payload UserDto user) {
        log.info("user id={} disconnected", user.getId());
        return ConnectEvent.builder().userId(user.getId()).connect(false).build();
    }

    @MessageMapping("/read_chat")
    @Transactional
    public ReadNotification onMarkChatAsRead(@Payload ReadNotification notification) {
        log.info("user id={} has read chat id={}", notification.getUserId(), notification.getChatId());
        chatService.processReadNotification(notification);
        return notification;
    }
}
