package com.ade.chat.repositories;

import com.ade.chat.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(
            "select c from chat c where c.isPrivate=true and " +
            "not exists (select 1 from c.members m where m.id not in ?1)"
    )
    Optional<Chat> findPrivateByMemberIds(List<Long> ids);

}
