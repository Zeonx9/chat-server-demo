package com.ade.chat.mappers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final GenericMapper<Message, MessageDto> mapper;

    public MessageDto toDto(Message message) {
        return mapper.toDto(message, MessageDto.class);
    }

    public Message toEntity(MessageDto dto) {
        return mapper.toEntity(dto, Message.class);
    }

    public List<MessageDto> toDtoList(List<Message> messageList) {
        return mapper.toDtoList(messageList, MessageDto.class);
    }
}
