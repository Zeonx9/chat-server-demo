package com.ade.chat.controllers;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.User;
import com.ade.chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat_api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user")
    public User getUserByNameOrCreate(@RequestParam String name) {
        return userService.getUserByNameOrCreate(name);
    }

    @GetMapping("/users/{id}/chats")
    public List<Chat> getUserChats(@PathVariable Long id) {
        return userService.getUserChats(id);
    }

    @PostMapping("/user")
    public void createUser(@RequestBody User newUser) {
        userService.createUser(newUser);
    }
}
