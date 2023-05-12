package com.ade.chat.mappers;

import com.ade.chat.domain.Chat;
import com.ade.chat.dtos.ChatDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper extends GenericMapper<Chat, ChatDto>{
    protected ChatMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<Chat> getEntityClass() {
        return Chat.class;
    }

    @Override
    protected Class<ChatDto> getDtoClass() {
        return ChatDto.class;
    }
}
