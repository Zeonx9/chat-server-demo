package com.ade.chat.mappers;

import com.ade.chat.domain.Message;
import com.ade.chat.dtos.MessageDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper extends GenericMapper<Message, MessageDto> {
    public MessageMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<Message> getEntityClass() {
        return Message.class;
    }

    @Override
    protected Class<MessageDto> getDtoClass() {
        return MessageDto.class;
    }
}
