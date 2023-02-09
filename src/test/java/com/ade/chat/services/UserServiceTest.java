package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.User;
import com.ade.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
    }

    @Test
    void canGetAllUsers() {
        // when
        underTest.getAllUsers();

        // then
        verify(userRepository).findAll();
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
                .hasMessageContaining("No user with given id:" + user.getId());
    }

    @Test
    void canCreateUser() {
        // given
        User user = User.builder().username("Artem").build();

        // when
        underTest.createUser(user);

        // then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        var capturedUser = argumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void exceptionIsThrownWhenCreateStudentThatExists() {
        // given
        User user = User.builder().username("Artem").build();
        given(userRepository.existsByUsername(user.getUsername()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> underTest.createUser(user))
                .hasMessageContaining("Name:" + user.getUsername() + " is taken already");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserChats() {
        // given
        User u1 = User.builder().username("Artem").build(),
                u2 = User.builder().username("Egor").build();
        Set<Chat> chatSet = Set.of(new Chat(true, Set.of(u1, u2)));
        u1.setChats(chatSet);
        u2.setChats(chatSet);

        given(userRepository.findById(u1.getId()))
                .willReturn(Optional.of(u1));

        // when
        var chats = underTest.getUserChats(u1.getId());

        // then
        assertThat(chats).isEqualTo(List.copyOf(chatSet));
    }

    @Test
    void getUserByNameWhenExist() {
        //given
        User user = User.builder().username("Artem").build();
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));

        // when
        underTest.getUserByNameOrCreate(user.getUsername());

        // then
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByNameWhenNotExist() {
        // given
        String name = "Artem";
        given(userRepository.findByUsername(name))
                .willReturn(Optional.empty());

        // when
        underTest.getUserByNameOrCreate(name);

        // then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        var capturedUser = argumentCaptor.getValue();

        assertThat(capturedUser.getUsername()).isEqualTo(name);
    }
}