package com.ade.chat.mappers;


import com.ade.chat.domain.User;
import com.ade.chat.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final GenericMapper<User, UserDto> mapper;

    public UserDto toDto(User user) {
        return mapper.toDto(user, UserDto.class);
    }

    public List<UserDto> toDtoList(List<User> userList) {
        return mapper.toDtoList(userList, UserDto.class);
    }
}
