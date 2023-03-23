package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.repositories.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepo;
    @Mock private UserService userService;
    private ChatService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChatService(chatRepo, userService);
    }

    private void givenRepositoryReturnsChat(Chat chat) {
        given(chatRepo.findById(chat.getId())).willReturn(Optional.of(chat));
    }

    private void givenServiceReturnsUser(User user) {
        given(userService.getUserByIdOrException(user.getId())).willReturn(user);
    }

    @Test
    void canFindChatByIdIfExists() {
        //given
        Chat chat = new Chat();
        givenRepositoryReturnsChat(chat);
        // when
        Chat foundChat = underTest.getChatByIdOrException(chat.getId());
        // then
        assertThat(foundChat).isEqualTo(chat);
    }

    @Test
    void findAbsentChatThrowsException() {
        //given
        Chat chat = new Chat();
        given(chatRepo.findById(chat.getId())).willReturn(Optional.empty());
        //when & then
        assertThatThrownBy(() -> underTest.getChatByIdOrException(chat.getId()))
                .hasMessageContaining("No chat with such id: " + chat.getId())
                .isInstanceOf(ChatNotFoundException.class);
    }

    private Chat configureCommonChatBetween(User u1, User u2) {
        Chat chat = Chat.builder().members(Set.of(u1, u2)).isPrivate(true).build();
        u1.getChats().add(chat);
        u2.getChats().add(chat);
        return chat;
    }

    @Test
    void canGetMessages() {
        //given
        Chat chat = new Chat();
        givenRepositoryReturnsChat(chat);
        //when
        List<Message> messages = underTest.getMessages(chat.getId(), null);
        //then
        assertThat(messages).isEqualTo(chat.getMessages());
    }

}