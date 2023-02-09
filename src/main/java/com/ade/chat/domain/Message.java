package com.ade.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity(name = "message")
@Table(
        name = "messages",
        indexes = @Index(name = "chat_id_index", columnList = "chat_id")
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    @Id
    @SequenceGenerator(name = "message_sequence", sequenceName = "message_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "message_sequence")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "chat_id",
            referencedColumnName = "id"
    )
    @JsonIgnore
    private Chat chat;

    public Message(String text) {
        this.text = text;
    }
}