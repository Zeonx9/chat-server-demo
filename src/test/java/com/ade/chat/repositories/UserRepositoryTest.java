package com.ade.chat.repositories;

import com.ade.chat.entities.User;
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
        User user = new User(name);
        underTest.save(user);

        // when
        Optional<User> foundUser = underTest.findByName(name);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);

    }

    @Test
    void itShouldCheckTheExistenceOfUserByName() {
        // given
        User user = new User("Artem");
        underTest.save(user);

        //when
        Boolean result = underTest.existsByName(user.getName());

        //then
        assertThat(result).isTrue();
    }

    @Test
    void itShouldCheckTheAbsenceOfUserByName() {
        // given
        User user = new User("Artem");

        //when
        Boolean result = underTest.existsByName(user.getName());

        //then
        assertThat(result).isFalse();
    }

}