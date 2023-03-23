package com.ade.chat.repositories;

import com.ade.chat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String name);

    Boolean existsByUsername(String name);

    List<User> findByCompany_Id(Long id);
}
