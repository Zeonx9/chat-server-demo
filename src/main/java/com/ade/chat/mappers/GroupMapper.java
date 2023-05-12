package com.ade.chat.mappers;

import com.ade.chat.domain.Group;
import com.ade.chat.dtos.GroupDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper extends GenericMapper<Group, GroupDto> {
    public GroupMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<Group> getEntityClass() {
        return Group.class;
    }

    @Override
    protected Class<GroupDto> getDtoClass() {
        return GroupDto.class;
    }
}
