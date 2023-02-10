package com.ade.chat.mappers;

import com.ade.chat.domain.Chat;
import com.ade.chat.dtos.ChatDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatMapper {

    private final ModelMapper mapper;

    public ChatDto toDto(Chat chat) {
        return Objects.isNull(chat) ? null : mapper.map(chat, ChatDto.class);
    }

    public Chat toEntity(ChatDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Chat.class);
    }

    public List<ChatDto> toDtoList(List<Chat> chatList) {
        return chatList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
