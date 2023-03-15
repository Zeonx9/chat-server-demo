package com.ade.chat.mappers;


import com.ade.chat.domain.User;
import com.ade.chat.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends GenericMapper<User, UserDto>{
    public UserMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected Class<UserDto> getDtoClass() {
        return UserDto.class;
    }
}
