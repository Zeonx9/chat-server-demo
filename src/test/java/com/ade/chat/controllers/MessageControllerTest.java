package com.ade.chat.controllers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.ChatService;
import com.ade.chat.services.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MessageController.class)
@ContextConfiguration(classes = {SecurityConfigurer.class, MessageController.class})
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private MessageService messageService;
    @MockBean private MessageMapper messageMapper;
    @MockBean private ChatService chatService;

    @Test
    @WithMockUser(value = "spring")
    void canSendAMessage() throws Exception {
        MessageDto messageDto = MessageDto.builder().text("text").build();
        Message message = new Message();
        Message out = Message.builder().id(34L).build();
        given(messageMapper.toEntity(messageDto)).willReturn(message);
        given(messageService.sendMessage(1L, 2L, message)).willReturn(out);

        mockMvc.perform(post("/chat_api/v1/users/1/chats/2/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(messageDto))
                .with(csrf())
        ).andExpect(status().isOk());

        verify(chatService).updateLastMessage(2L, out);
        verify(messageMapper).toDto(out);
    }

    private String asJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
