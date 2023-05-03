package com.ade.chat.repositories;

import com.ade.chat.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("update user u set u.password = ?1 where u.id = ?2")
    int updatePasswordById(String password, Long id);

    Optional<User> findByUsername(String name);

    Boolean existsByUsername(String name);

    List<User> findByCompany_Id(Long id, Sort sort);

}
