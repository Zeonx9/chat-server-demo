package com.ade.chat.controllers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Отвечает за операции с пользователями, все методы требуют уровень доступа USER
 */
@RestController
@RequestMapping("chat_api/v1")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    /**
     * Получает список всех пользователей системы
     * @return список всех доступных пользователей в базе данных
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsers()));
    }

    /**
     * Получает список пользователей, принадлежащих указанной компании
     * @param id идентификатор компании
     * @return список всех доступных пользователей в базе данных
     */
    @GetMapping("/company/{id}/users")
    public ResponseEntity<List<UserDto>> getCompanyUsers(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsersFromCompany(id)));
    }

    /**
     * Получает список чатов доступных пользователю с заданным id
     * @param id id пользователя для поиска
     * @return список доступных чатов
     * @throws com.ade.chat.exception.UserNotFoundException если передан не существующий айди пользователя
     */
    @GetMapping("/users/{id}/chats")
    public ResponseEntity<List<ChatDto>> getUserChats(@PathVariable Long id) {
        return ResponseEntity.ok(chatMapper.toDtoList(userService.getUserChats(id)));
    }

    /**
     * Получает все сообщения, ранее не полученные этим пользователем
     * @param id идентификатор пользователя
     * @return список сообщений
     * @throws com.ade.chat.exception.UserNotFoundException если неверен идентификатор
     */
    @Transactional
    @GetMapping("/users/{id}/undelivered_messages")
    public ResponseEntity<List<MessageDto>> getUndeliveredMessages(@PathVariable Long id) {
        List<Message> result = userService.getUndeliveredFor(id);
        userService.markAsDelivered(result, id);
        return ResponseEntity.ok(messageMapper.toDtoList(result));
    }
}
