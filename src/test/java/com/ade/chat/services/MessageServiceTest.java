package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.MessageRepository;
import com.ade.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepo;
    @Mock private UserRepository userRepo;
    @Mock private ChatRepository chatRepo;
    private MessageService underTest;

    @BeforeEach
    void setUp() {
        var userService = new UserService(userRepo);
        var chatService = new ChatService(chatRepo, userService);
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

        given(userRepo.findById(user.getId())).willReturn(Optional.of(user));
        given(chatRepo.findById(chat.getId())).willReturn(Optional.of(chat));

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

        given(userRepo.findById(user.getId())).willReturn(Optional.of(user));
        given(chatRepo.findById(chat.getId())).willReturn(Optional.of(chat));

        //when & then
        assertThatThrownBy(() -> underTest.sendMessage(user.getId(), chat.getId(), msg))
                .hasMessageContaining( "This user: " + user.getId() + " is not a member of a chat: " + chat.getId());
    }
}