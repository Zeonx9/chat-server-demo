package com.ade.chat.controllers;

import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Spring REST контроллер, отвечающий за операции над сообщениями
 */
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    /**
     * POST реквест с полным путем chat_api/v1/users/{userId}/chats/{chatId}/message
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param userId идентификатор пользователя, отправляющего сообщение
     * @param msgDto сообщение, которое будет отправлено
     * @throws com.ade.chat.exception.UserNotFoundException если неверное айди пользователя
     * @throws com.ade.chat.exception.ChatNotFoundException если неверное айди чата
     * @throws com.ade.chat.exception.NotAMemberException если пользователь не состоит в чате
     */
    @PostMapping("/users/{userId}/chats/{chatId}/message")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long userId,
            @PathVariable Long chatId,
            @RequestBody MessageDto msgDto
    ) {
        return ResponseEntity.ok(
                messageMapper.toDto(
                        messageService.sendMessage(
                                userId,
                                chatId,
                                messageMapper.toEntity(msgDto)
                        )
                )
        );
    }
}
