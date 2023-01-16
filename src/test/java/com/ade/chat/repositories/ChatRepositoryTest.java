package com.ade.chat.repositories;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository underTest;
    @Autowired
    private UserRepository userRepo;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    @Disabled
    void findPrivateByMemberIds() {
        // given
        User
                u1 = new User(1L, "Artem", null, null),
                u2 = new User(2L, "Egor", null, null);
        userRepo.save(u1);
        userRepo.save(u2);

        Chat privateChat = new Chat(1L, true, Set.of(u1, u2), null);
        underTest.save(privateChat);

        List<Long> ids = List.of(u1.getId(), u2.getId());

        // when
        Optional<Chat> foundPrivateChat = underTest.findPrivateByMemberIds(ids);

        // then
        assertThat(foundPrivateChat).isPresent();
    }
}