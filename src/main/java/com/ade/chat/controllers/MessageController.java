package com.ade.chat.controllers;

import com.ade.chat.domain.Message;
import com.ade.chat.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Spring REST контроллер, отвечающий за операции над сообщениями
 */
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * POST реквест с полным путем chat_api/v1/users/{userId}/chats/{chatId}/message
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param userId идентификатор пользователя, отправляющего сообщение
     * @param msg сообщение, которое будет отправлено
     */
    @PostMapping("/users/{userId}/chats/{chatId}/message")
    public void sendMessage(@PathVariable Long userId,
                            @PathVariable Long chatId,
                            @RequestBody Message msg) {
        messageService.sendMessage(userId, chatId, msg);
    }

    /**
     * POST реквест с полным путем chat_api/v1/users/{fromUserId}/message/users/{toUserId}
     * @param fromUserId идентификатор отправителя
     * @param toUserId идентификатор получаетля
     * @param msg сообщение, которое будет отправлено в приватный чат между указанными пользователями
     */
    @PostMapping("/users/{fromUserId}/message/users/{toUserId}")
    public void sendPrivateMessage(@PathVariable Long fromUserId,
                                   @PathVariable Long toUserId,
                                   @RequestBody Message msg) {
        messageService.sendPrivateMessage(fromUserId, toUserId, msg);
    }
}
