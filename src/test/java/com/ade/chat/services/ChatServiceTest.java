package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Group;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.AbsentGroupInfoException;
import com.ade.chat.exception.ChatNotFoundException;
import com.ade.chat.exception.NotAMemberException;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.UnreadCounterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
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
class ChatServiceTest {

    @Mock private ChatRepository chatRepo;
    @Mock private UserService userService;
    @Mock private ChatMapper chatMapper;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private UnreadCounterRepository counterRepository;
    private ChatService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChatService(chatRepo, userService, chatMapper, messagingTemplate, counterRepository);
    }

    private void givenRepositoryReturnsChat(Chat chat) {
        given(chatRepo.findById(chat.getId())).willReturn(Optional.of(chat));
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
    @Test
    void canGetMessages() {
        //given
        Chat chat = new Chat();
        User user = User.builder().id(1L).build();
        givenRepositoryReturnsChat(chat);
        given(userService.getUserByIdOrException(user.getId())).willReturn(user);
        //when
        List<Message> messages = underTest.getMessages(chat.getId(), user.getId());
        //then
        assertThat(messages).isEqualTo(chat.getMessages());
    }

    @Test
    void canGetExistingChatBetweenUsers() {
        Chat c = new Chat();
        User u1 = User.builder().id(1L).build(), u2 = User.builder().id(2L).build();
        given(chatRepo.findByMembers_IdAndIsPrivateTrue(u1.getId())).willReturn(new HashSet<>(Set.of(c)));
        given(chatRepo.findByMembers_IdAndIsPrivateTrue(u2.getId())).willReturn(Set.of(c));

        Chat result = underTest.createOrGetPrivateChat(u1.getId(), u2.getId());
        assertThat(result).isEqualTo(c);
        verify(chatRepo, never()).save(any());
    }

    @Test
    void newChatIsCreatedIfDidNotExist() {
        User u1 = User.builder().id(1L).build(), u2 = User.builder().id(2L).build();
        given(chatRepo.findByMembers_IdAndIsPrivateTrue(u1.getId())).willReturn(new HashSet<>());
        given(chatRepo.findByMembers_IdAndIsPrivateTrue(u2.getId())).willReturn(new HashSet<>());

        underTest.createOrGetPrivateChat(u1.getId(), u2.getId());
        verify(chatRepo).save(any());
    }

    @Test
    void canUpdateLastMessage() {
        Message msg = new Message();
        Chat chat = Chat.builder().id(1L).build();
        msg.setDateTime(LocalDateTime.now());
        given(chatRepo.findById(1L)).willReturn(Optional.of(chat));
        underTest.updateLastMessage(chat, msg);
        verify(chatRepo).updateLastMessageById(msg, 1L);
    }

    @Test
    void throwExceptionWhenGroupIsNull() {
        assertThatThrownBy(() -> underTest.createGroupChat(List.of(1L), null))
                .isInstanceOf(AbsentGroupInfoException.class);
    }

    @Test
    void throwExceptionWhenNotAMember() {
        List<Long> ids = List.of(1L, 2L);
        Group group = Group.builder().creator(User.builder().id(3L).build()).build();
        assertThatThrownBy(() -> underTest.createGroupChat(ids, group))
                .isInstanceOf(NotAMemberException.class);
    }

    @Test
    void canCreateGroupChat() {
        List<Long> ids = List.of(1L, 2L);
        Group group = Group.builder().creator(User.builder().id(1L).build()).build();
        underTest.createGroupChat(ids, group);
        verify(chatRepo).save(any());
    }

}