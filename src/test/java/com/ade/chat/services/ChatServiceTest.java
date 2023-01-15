package com.ade.chat.services;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepo;
    @Mock private UserRepository userRepo;
    private ChatService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChatService(chatRepo,  new UserService(userRepo));
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
    void canFindPrivateChatByIdList() {
        // given
        List<Long> ids = List.of(1L, 2L, 3L);

        // when & then
        assertThatThrownBy(() -> underTest.getPrivateChatByMemberIds(ids))
                .hasMessageContaining("private chat is only for 2 users, not for " + ids.size());
    }

    @Test
    void canCreateNewChat() {
        //given
        Boolean isPrivate = false;
        List<Long> ids = List.of(1L);
        ids.forEach((id) ->
                given(userRepo.findById(id)).willReturn(Optional.of(new User()))
        );


        //when
        var chat = underTest.createChat(ids, isPrivate);

        // then
        ArgumentCaptor<Chat> captor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepo).save(captor.capture());
        var capturedChat = captor.getValue();

        assertThat(capturedChat).isEqualTo(chat);
    }

    @Test
    void throwExceptionWhenChatAlreadyExists() {
        //given
        Boolean isPrivate = true;
        Chat chat = new Chat();
        List<Long> ids = List.of(1L, 2L);
        given(chatRepo.findPrivateByMemberIds(ids))
                .willReturn(Optional.of(chat));

        //when & then
        assertThatThrownBy(() -> underTest.createChat(ids, isPrivate))
                .hasMessageContaining("This chat already exists");
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
}