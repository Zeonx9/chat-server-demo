package com.ade.chat.controllers;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Group;
import com.ade.chat.domain.Message;
import com.ade.chat.dtos.GroupDto;
import com.ade.chat.dtos.GroupRequest;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.GroupMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.ChatService;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {SecurityConfigurer.class, ChatController.class})
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private ChatService chatService;
    @MockBean private GroupMapper groupMapper;
    @MockBean private ChatMapper chatMapper;
    @MockBean private MessageMapper messageMapper;

    @Test
    @WithMockUser(value = "spring")
    public void canGetPrivateChat() throws Exception {
        Chat chat = new Chat();
        given(chatService.createOrGetPrivateChat(1L, 2L)).willReturn(chat);
        mockMvc.perform(get("/chat_api/v1/private_chat/1/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(chatMapper).toDto(chat);
    }

    @Test
    @WithMockUser(value = "spring")
    public void canCreateGroupChat() throws Exception {
        GroupDto info = GroupDto.builder()
                .name("first group")
                .creator(UserDto.builder().id(1L).build())
                .build();
        List<Long> ids = List.of(1L, 2L, 3L);
        GroupRequest request = GroupRequest.builder().groupInfo(info).ids(ids).build();

        Group gr = new Group();
        given(groupMapper.toEntity(request.getGroupInfo())).willReturn(gr);
        Chat chat = new Chat();
        given(chatService.createGroupChat(request.getIds(), gr)).willReturn(chat);

        mockMvc.perform(
                post("/chat_api/v1/group_chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request))
                        .with(csrf())
                )
                .andExpect(status().isOk());
        verify(chatMapper).toDto(chat);
    }

    @Test
    @WithMockUser(value = "spring")
    public void canGetMessages() throws Exception {
        List<Message> messages = List.of(new Message());
        given(chatService.getMessages(1L)).willReturn(messages);
        mockMvc.perform(get("/chat_api/v1/chats/1/messages?userId=2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(messageMapper).toDtoList(messages);
    }

    @Test
    @WithMockUser(value = "spring")
    public void canGetChatById() throws Exception {
        Chat chat = new Chat();
        given(chatService.getChatByIdOrException(1L)).willReturn(chat);
        mockMvc.perform(get("/chat_api/v1/chats/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(chatMapper).toDto(chat);
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
