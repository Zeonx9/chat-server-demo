package com.ade.chat.mappers;


import com.ade.chat.domain.User;
import com.ade.chat.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper mapper;

    public UserDto toDto(User user) {
        return Objects.isNull(user) ? null : mapper.map(user, UserDto.class);
    }

    public User toEntity(UserDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, User.class);
    }

    public List<UserDto> toDtoList(List<User> userList) {
        return userList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
