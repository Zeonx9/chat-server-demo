package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.repositories.MessageRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepo;
    @Mock private UserService userService;
    @Mock private ChatService chatService;
    private MessageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageService(
                messageRepo,
                chatService,
                userService
        );
    }

    @Test
    void canSendMessage() {
        // given
        User user = new User();
        Chat chat = new Chat();
        Message msg = new Message();
        msg.setText("message");

        chat.getMembers().add(user);

        given(userService.getUserByIdOrException(user.getId())).willReturn(user);
        given(chatService.getChatByIdOrException(chat.getId())).willReturn(chat);

        // when
        underTest.sendMessage(user.getId(), chat.getId(), msg);

        // then
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepo).save(argumentCaptor.capture());
        var capturedMsg = argumentCaptor.getValue();

        assertThat(capturedMsg).isEqualTo(msg);
    }

    @Test
    void throwsExceptionWhenUserIsNotMemberOfChat() {
        // given
        User user = new User();
        Chat chat = new Chat();
        Message msg = new Message();
        msg.setText("message");

        given(userService.getUserByIdOrException(user.getId())).willReturn(user);
        given(chatService.getChatByIdOrException(chat.getId())).willReturn(chat);

        //when & then
        assertThatThrownBy(() -> underTest.sendMessage(user.getId(), chat.getId(), msg))
                .hasMessageContaining( "This user: " + user.getId() + " is not a member of a chat: " + chat.getId());
    }

    @Test
    void sendPrivateMessageToExisting() {
        //given
        User u1 = new User(1L, null, null, null),
             u2 = new User(2L, null, null, null);
        Chat chat = new Chat();

        given(userService.getUserByIdOrException(u1.getId())).willReturn(u1);
        given(userService.getUserByIdOrException(u2.getId())).willReturn(u2);
        given(chatService.privateChatBetweenUsers(u1, u2)).willReturn(Optional.of(chat));
        Message msg = new Message("text");

        //when
        underTest.sendPrivateMessage(u1.getId(), u2.getId(), msg);

        //then
        verify(messageRepo).save(msg);
    }

    @Test
    void sendPrivateMessageWithoutChat() {
        //given
        User u1 = new User(1L, null, null, Set.of()),
                u2 = new User(2L, null, null, Set.of());
        Chat chat = new Chat();

        given(userService.getUserByIdOrException(u1.getId())).willReturn(u1);
        given(userService.getUserByIdOrException(u2.getId())).willReturn(u2);
        given(chatService.privateChatBetweenUsers(u1, u2)).willReturn(Optional.empty());
        given(chatService.createChat(List.of(u1.getId(), u2.getId()), true)).willReturn(chat);
        Message msg = new Message("text");

        //when
        underTest.sendPrivateMessage(u1.getId(), u2.getId(), msg);

        //then
        verify(messageRepo).save(msg);
    }
}