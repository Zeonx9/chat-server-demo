package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
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

    @Test
    void canFindChatByIdIfExists() {
        //given
        Chat chat = new Chat();
        given(chatRepo.findById(chat.getId())).willReturn(Optional.of(chat));

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
                .hasMessageContaining("No chat with such id: " + chat.getId());
    }

    @Test
    void canGetMessages() {
        //given
        Chat chat = new Chat();
        given(chatRepo.findById(chat.getId()))
                .willReturn(Optional.of(chat));

        //when
        List<Message> messages = underTest.getMessages(chat.getId());

        //then
        assertThat(messages).isEqualTo(chat.getMessages());
    }

    @Test
    void canFindCommonChat() {
        //given
        User u1 = new User(1L, null, null, null),
             u2 = new User(2L, null, null, null);
        Chat chat = new Chat(1L, true, Set.of(u1, u2), null);
        u1.setChats(Set.of(chat));
        u2.setChats(Set.of(chat));

        given(userService.getUserByIdOrException(u1.getId())).willReturn(u1);
        given(userService.getUserByIdOrException(u2.getId())).willReturn(u2);

        // when
        Optional<Chat> commonChat =
                underTest.privateChatBetweenUsersWithIds(List.of(u1.getId(), u2.getId()));

        // then
        assertThat(commonChat).isPresent();
        assertThat(commonChat.get()).isEqualTo(chat);
    }

    @Test
    void noCommonChatNotFound() {
        //given
        User u1 = new User(1L, null, null, Set.of()),
                u2 = new User(2L, null, null, Set.of());

        given(userService.getUserByIdOrException(u1.getId())).willReturn(u1);
        given(userService.getUserByIdOrException(u2.getId())).willReturn(u2);

        // when
        Optional<Chat> commonChat =
                underTest.privateChatBetweenUsersWithIds(List.of(u1.getId(), u2.getId()));

        // then
        assertThat(commonChat).isEmpty();
    }

    @Test
    void exceptionWhenMoreNot2Ids() {
        User u1 = new User(1L, null, null, null),
                u2 = new User(2L, null, null, null);
        Chat chat = new Chat(1L, true, Set.of(u1, u2), null);
        u1.setChats(Set.of(chat));
        u2.setChats(Set.of(chat));
        given(userService.getUserByIdOrException(u1.getId())).willReturn(u1);
        given(userService.getUserByIdOrException(u2.getId())).willReturn(u2);
        //when & then
        assertThatThrownBy(() -> underTest.createChat(List.of(u1.getId(), u2.getId()), true))
                .hasMessageContaining("This chat already exists");

    }

    @Test
    void exceptionWhenCreatePrivateWithWrongNumberOfUsers() {
        //when & then
        assertThatThrownBy(() -> underTest.createChat(List.of(), true))
                .hasMessageContaining("size of id list for private chat must equals 2");

    }
}