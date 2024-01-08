package com.ade.chat.controllers;

import com.ade.chat.dtos.ConnectEvent;
import com.ade.chat.dtos.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {
    @MessageMapping("/connect")
    @SendTo("/topic/connection")
    public ConnectEvent onUserConnected(@Payload UserDto user) {
        log.info("user with id: " + user.getId() + " connected to server");
        return ConnectEvent.builder().userId(user.getId()).connect(true).build();
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/connection")
    public ConnectEvent onUserDisconnected(@Payload UserDto user) {
        log.info("user with id: " + user.getId() + " disconnected to server");
        return ConnectEvent.builder().userId(user.getId()).connect(false).build();
    }
}
