package com.ade.chat.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private Boolean isPrivate;
    private List<UserDto> members;
    private GroupDto group;
    private MessageDto lastMessage;
}
