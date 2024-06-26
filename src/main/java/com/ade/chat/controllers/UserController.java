package com.ade.chat.controllers;

import com.ade.chat.domain.User;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.UnreadCounterDto;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.UnreadCounterMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UnreadCounterMapper counterMapper;

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

    /**
     * Получает счетчики непрочитанных сообщений в чатах для пользователя
     * @param id идентификатор пользователя
     * @return список счетчиков
     */
    @GetMapping("/users/{id}/chats/unread")
    public ResponseEntity<List<UnreadCounterDto>> getUnreadInChatsForUser(@PathVariable Long id) {
        return ResponseEntity.ok(counterMapper.toDtoList(userService.getChatCountersByUserId(id)));
    }

    /**
     * Обновляет пользователю аватарку
     * @param userId идентификатор пользователя
     * @param file прикрепленный файл с новым изображением
     * @return Обновленного пользователя
     */
    @PostMapping(value = "/users/{id}/profile_photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> uploadNewProfilePhoto(
            @PathVariable("id") Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        User updatedUser = userService.uploadProfilePhoto(userId, file);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }
}
