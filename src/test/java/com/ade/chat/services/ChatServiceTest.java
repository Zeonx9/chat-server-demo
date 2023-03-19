package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.IllegalMemberCount;
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
    void canGetPrivateByIds() {
        //given
        List<Long> ids = List.of(1L, 2L);
        User u1 = User.builder().id(ids.get(0)).build();
        User u2 = User.builder().id(ids.get(1)).build();
        givenServiceReturnsUser(u1);
        givenServiceReturnsUser(u2);
        Chat chat = configureCommonChatBetween(u1, u2);
        //when
        Optional<Chat> commonChat = underTest.privateChatBetweenUsersWithIds(ids);
        //then
        assertThat(commonChat.isPresent()).isTrue();
        assertThat(commonChat.get()).isEqualTo(chat);

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

    @Test
    void returnExistingWhenTryToCreateExisting() {
        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        Chat chat = configureCommonChatBetween(u1, u2);
        givenServiceReturnsUser(u1);
        givenServiceReturnsUser(u2);
        //when
        var returned = underTest.createOrGetChat(List.of(u1.getId(), u2.getId()), true);

        //then
        assertThat(returned).isEqualTo(chat);

    }

    @Test
    void exceptionWhenCreatePrivateWithWrongNumberOfUsers() {
        //when & then
        assertThatThrownBy(() -> underTest.createOrGetChat(List.of(), true))
                .hasMessageContaining("size of id list for private chat must equals 2")
                .isInstanceOf(IllegalMemberCount.class);
    }
}