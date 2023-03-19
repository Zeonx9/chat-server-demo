package com.ade.chat.controllers;

import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Spring REST контроллер, отвечающий за опереции с пользователями
 */
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    /**
     * GET реквест с полным путем /chat_api/v1/users
     * @return список всех доступных пользователей в базе данных
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsers()));
    }

    /**
     * GET реквест с полным путем /chat_api/v1/users/{id}/chats
     * получает список чатов доступных пользователю с заданным id
     * @param id id пользователя для поиска
     * @return список доступных чатов
     * @throws com.ade.chat.exception.UserNotFoundException если передан не существующий айди пользователя
     */
    @GetMapping("/users/{id}/chats")
    public ResponseEntity<List<ChatDto>> getUserChats(@PathVariable Long id) {
        return ResponseEntity.ok(chatMapper.toDtoList(userService.getUserChats(id)));
    }

    /**
     * GET реквест, с полным путем /chat_api/v1/user/{id}/undelivered_messages
     * получает все сообщения, ранее не полученные этим пользователем
     * @param id идентификатор пользователя
     * @return список сообщений
     * @throws com.ade.chat.exception.UserNotFoundException если неверен идентификатор
     */
    @GetMapping("/users/{id}/undelivered_messages")
    public ResponseEntity<List<MessageDto>> getUndeliveredMessages(@PathVariable Long id) {
        return ResponseEntity.ok(
                messageMapper.toDtoList(
                        userService.getUndeliveredMessagesAndMarkAsDelivered(id)
                )
        );
    }
}
