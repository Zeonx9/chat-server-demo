package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unread_counter")
public class UnreadCounter {
    @EmbeddedId
    @Builder.Default
    private ChatUserKey id = new ChatUserKey();

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "count")
    @Builder.Default
    private Integer count = 0;
}
