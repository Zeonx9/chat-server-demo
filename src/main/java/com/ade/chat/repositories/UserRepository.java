package com.ade.chat.repositories;

import com.ade.chat.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from user u where u.name = ?1")
    Optional<User> findByName(String name);
}
