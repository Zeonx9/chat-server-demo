package com.ade.chat.controllers;

import com.ade.chat.entities.Message;
import com.ade.chat.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("chat_api/v1")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/users/{userId}/chats/{chatId}/message")
    public void sendMessage(@PathVariable Long userId,
                            @PathVariable Long chatId,
                            @RequestBody Message msg) {
        messageService.sendMessage(userId, chatId, msg);
    }
}
