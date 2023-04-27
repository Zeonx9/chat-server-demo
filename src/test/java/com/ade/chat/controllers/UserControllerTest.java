package com.ade.chat.controllers;

import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.MessageMapper;
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
    @MockBean private MessageMapper messageMapper;

    @Test
    @WithMockUser(value = "spring")
    public void contextLoads() throws Exception {
        mockMvc.perform(get("/chat_api/v1/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

}
