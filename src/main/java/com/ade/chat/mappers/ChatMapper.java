package com.ade.chat.mappers;

import com.ade.chat.domain.Chat;
import com.ade.chat.dtos.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMapper {
    private final GenericMapper<Chat, ChatDto> mapper;

    public ChatDto toDto(Chat chat) {
        return mapper.toDto(chat, ChatDto.class);
    }

    public List<ChatDto> toDtoList(List<Chat> chatList) {
        return mapper.toDtoList(chatList, ChatDto.class);
    }
}
