package com.ade.chat.controllers;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.User;
import com.ade.chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring REST контроллер, отвечающий за опереции с пользователями
 */
@RestController
@RequestMapping("chat_api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET реквест с полным путем /chat_api/v1/users
     * @return список всех доступных пользователей в базе данных
     */
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET реквест с полным путем /chat_api/v1/users
     * получает информацию о пользователе по его имени или создает нового, если имя не было занято
     * @param name логин(имя) пользователя для поиска
     * @return всегда одного пользователя с заданным именем
     */
    @GetMapping("/user")
    public User getUserByNameOrCreate(@RequestParam String name) {
        return userService.getUserByNameOrCreate(name);
    }

    /**
     * GET реквест с полным путем /chat_api/v1/users/{id}/chats
     * получает список чатов доступных пользователю с заданным id
     * @param id id пользователя для поиска
     * @return список доступных чатов
     */
    @GetMapping("/users/{id}/chats")
    public List<Chat> getUserChats(@PathVariable Long id) {
        return userService.getUserChats(id);
    }

    /**
     * POST реквест с полным путем /chat_api/v1/user
     * создает пользователя с заданными характеристиками
     * @param newUser пользователь, информация о котором передана через тело запроса
     */
    @PostMapping("/user")
    public void createUser(@RequestBody User newUser) {
        userService.createUser(newUser);
    }
}
