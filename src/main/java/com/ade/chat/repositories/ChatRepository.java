package com.ade.chat.repositories;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Transactional
    @Modifying
    @Query("update chat c set c.lastMessage = ?1 where c.id = ?2")
    void updateLastMessageById(Message lastMessage, Long id);

    @Query("select c from chat c inner join c.members members where members.id = ?1 and c.isPrivate = true")
    Set<Chat> findByMembers_IdAndIsPrivateTrue(Long id);


}
