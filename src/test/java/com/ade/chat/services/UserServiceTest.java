package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.User;
import com.ade.chat.exception.UserNotFoundException;
import com.ade.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MinioService minioService;
    @Mock private JwtService jwtService;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, minioService, jwtService);
    }

    @Test
    void canGetAllUsers() {
        // when
        underTest.getAllUsers();

        // then
        verify(userRepository).findAll(Sort.by(Sort.Direction.ASC, "username"));
    }

    @Test
    void canGetAllUsersOnlyWithinCompany() {
        underTest.getAllUsersFromCompany(1L);
        verify(userRepository).findByCompany_Id(1L, Sort.by(Sort.Direction.ASC, "username"));
    }

    @Test
    void canGetExistingUserById() {
        // given
        User user = User.builder().username("Artem").build();
        given(userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));

        // when
        User foundUser = underTest.getUserByIdOrException(user.getId());

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void findAbsentUserWillThrowException() {
        // given
        User user = User.builder().username("Artem").build();
        given(userRepository.findById(user.getId()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> underTest.getUserByIdOrException(user.getId()))
                .hasMessageContaining("No user with given id:" + user.getId())
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserChats() {
        // given
        Set<Chat> chats = Set.of(new Chat());
        User u1 = User.builder()
                .username("Artem")
                .chats(chats)
                .build();

        given(userRepository.findById(u1.getId()))
                .willReturn(Optional.of(u1));

        // when
        var returnedChats = underTest.getUserChats(u1.getId());

        // then
        assertThat(returnedChats).isEqualTo(List.copyOf(chats));
    }
}