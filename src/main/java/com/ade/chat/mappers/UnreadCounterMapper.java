package com.ade.chat.mappers;

import com.ade.chat.domain.UnreadCounter;
import com.ade.chat.dtos.UnreadCounterDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UnreadCounterMapper extends GenericMapper<UnreadCounter, UnreadCounterDto> {
    protected UnreadCounterMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<UnreadCounter> getEntityClass() {
        return UnreadCounter.class;
    }

    @Override
    protected Class<UnreadCounterDto> getDtoClass() {
        return UnreadCounterDto.class;
    }
}
