package com.ade.chat.repositories;

import com.ade.chat.containers.ContainersEnvironment;
import com.ade.chat.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends ContainersEnvironment {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckTheAbsenceOfUserByName() {
        // given
        User user = User.builder().username("Artem").build();

        //when
        Boolean result = underTest.existsByUsername(user.getUsername());

        //then
        assertThat(result).isFalse();
    }

}