package com.ade.chat.repositories;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.ChatUserKey;
import com.ade.chat.domain.UnreadCounter;
import com.ade.chat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UnreadCounterRepository extends JpaRepository<UnreadCounter, ChatUserKey> {
    @Transactional
    @Modifying
    @Query("update UnreadCounter u set u.count = u.count + 1 where u.chat = ?1 and u.user = ?2")
    void incrementCountByChatAndUser(Chat chat, User user);

    @Transactional
    @Modifying
    @Query("update UnreadCounter u set u.count = 0 where u.chat = ?1 and u.user = ?2")
    void setCountToZeroByChatAndUser(Chat chat, User user);

}
