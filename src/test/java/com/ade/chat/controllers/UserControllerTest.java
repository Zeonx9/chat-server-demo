package com.ade.chat.controllers;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.User;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {SecurityConfigurer.class, UserController.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private UserMapper userMapper;
    @MockBean private ChatMapper chatMapper;

    @Test
    @WithMockUser(value = "spring")
    public void canGetAllUsers() throws Exception {
        List<User> users = List.of(new User());
        given(userService.getAllUsers()).willReturn(users);
        mockMvc.perform(get("/chat_api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userMapper).toDtoList(users);
    }

    @Test
    @WithMockUser(value = "spring")
    public void canGetUsersFromCompany() throws Exception {
        List<User> users = List.of(new User());
        given(userService.getAllUsersFromCompany(1L)).willReturn(users);
        mockMvc.perform(get("/chat_api/v1/company/1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userMapper).toDtoList(users);
    }

    @Test
    @WithMockUser(value = "spring")
    public void canGetUserChats() throws Exception {
        List<Chat> chats = List.of(new Chat());
        given(userService.getUserChats(1L)).willReturn(chats);
        mockMvc.perform(get("/chat_api/v1/users/1/chats").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(chatMapper).toDtoList(chats);
    }

}
