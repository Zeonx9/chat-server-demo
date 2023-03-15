package com.ade.chat.repositories;

import com.ade.chat.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindTheStudentByName() {
        // given
        String name = "Artem";
        User user = User.builder().username(name).build();
        underTest.save(user);

        // when
        Optional<User> foundUser = underTest.findByUsername(name);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    void itShouldCheckTheExistenceOfUserByName() {
        // given
        User user = User.builder().username("Artem").build();
        underTest.save(user);

        //when
        Boolean result = underTest.existsByUsername(user.getUsername());

        //then
        assertThat(result).isTrue();
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