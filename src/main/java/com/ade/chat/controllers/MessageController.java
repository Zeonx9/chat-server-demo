package com.ade.chat.controllers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Отвечает за операции над сообщениями, все методы требую уровень доступа USER
 */
@RestController
@RequestMapping("chat_api/v1")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    /**
     * Сохраняет отправленное сообщение.
     * Уведомляет членов чата о новом сообщении.
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
        log.info("message from userId={} to chatId={}", userId, chatId);
        Message sent = messageService.sendMessage(userId, chatId, messageMapper.toEntity(msgDto));
        return ResponseEntity.ok(messageMapper.toDto(sent));
    }
}
