package com.ade.chat.controllers;

import com.ade.chat.domain.User;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.mappers.ChatMapper;
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
     * Изменяет поля пользователя на переданные значение, игнорирует пропуски (null)
     * обновляются поля: realName, surname, dateOfBirth
     * @param id Идентификатор пользователя для обновления полей
     * @param newUser содержит новые значения полей
     * @throws com.ade.chat.exception.UserNotFoundException если id не верный
     */
    @Transactional
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUserData(@PathVariable Long id, @RequestBody UserDto newUser) {
        User updatedUser = userService.updateUserData(id, newUser);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }
}
