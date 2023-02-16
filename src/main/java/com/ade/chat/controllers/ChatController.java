package com.ade.chat.controllers;

import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring REST контроллер, отвечающий за операции с чатами
 */
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    /**
     * POST реквест с полным путем chat_api/v1/chat
     * @param ids список ID пользоваетелей, которые будут участниками чата
     *            для приватных чатов обязательно только 2 ID в списке, иначе ошибка
     * @param isPrivate если true то будет создан приватный чат, в противном случае беседа
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatDto> createOrGetChat(
            @RequestBody List<Long> ids,
            @RequestParam(required = false) Boolean isPrivate) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.createOrGetChat(ids, isPrivate)));
    }

    /**
     * GET реквест с полным путем chat_api/v1/chats/{chatId}/messages
     * получает список сообщений из указанного чата
     * @param chatId идентификатор чата
     * @return список сообщений чата
     * @throws com.ade.chat.exception.ChatNotFoundException если нет чата с таким айди
     */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(messageMapper.toDtoList(chatService.getMessages(chatId, userId)));
    }
}
