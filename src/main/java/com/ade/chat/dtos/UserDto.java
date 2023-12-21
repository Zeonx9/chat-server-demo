package com.ade.chat.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String realName;
    private String surname;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Boolean isOnline;
    private String patronymic;
}
