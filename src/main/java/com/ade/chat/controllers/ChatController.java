package com.ade.chat.controllers;

import com.ade.chat.entities.Message;
import com.ade.chat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat_api/v1")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public void createChat(
            @RequestBody List<Long> ids,
            @RequestParam(required = false) Boolean isPrivate) {
        chatService.createChat(ids, isPrivate);
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<Message> getMessages(@PathVariable Long chatId) {
        return chatService.getMessages(chatId);
    }
}
