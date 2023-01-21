package com.ade.chat.controllers;

import com.ade.chat.entities.Message;
import com.ade.chat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring REST контроллер, отвечающий за операции с чатами
 */
@RestController
@RequestMapping("chat_api/v1")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * POST реквест с полным путем chat_api/v1/chat
     * @param ids список ID пользоваетелей, которые будут участниками чата
     *            для приватных чатов обязательно только 2 ID в списке, иначе ошибка
     * @param isPrivate если true то будет создан приватный чат, в противном случае беседа
     */
    @PostMapping("/chat")
    public void createChat(
            @RequestBody List<Long> ids,
            @RequestParam(required = false) Boolean isPrivate) {
        chatService.createChat(ids, isPrivate);
    }

    /**
     * GET реквест с полным путем chat_api/v1/chats/{chatId}/messages
     * получает список сообщений из указанного чата
     * @param chatId идентификатор чата
     * @return список сообщений чата
     */
    @GetMapping("/chats/{chatId}/messages")
    public List<Message> getMessages(@PathVariable Long chatId) {
        return chatService.getMessages(chatId);
    }
}
