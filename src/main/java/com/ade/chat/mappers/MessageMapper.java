package com.ade.chat.mappers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.MessageDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final ModelMapper mapper;

    public MessageDto toDto(Message message) {
        return Objects.isNull(message) ? null : mapper.map(message, MessageDto.class);
    }

    public Message toEntity(MessageDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Message.class);
    }

    public List<MessageDto> toDtoList(List<Message> messageList) {
        return messageList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
