package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.repositories.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepo;
    @Mock private UserService userService;
    @Mock private ChatService chatService;
    @Mock private ChatMessagingTemplate messagingTemplate;
    private MessageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageService(
                messageRepo,
                chatService,
                userService,
                messagingTemplate
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
                .hasMessageContaining( "This user: " + user.getId() + " is not a member of a chat: " + chat.getId())
                .isInstanceOf(NotAMemberException.class);
    }
}