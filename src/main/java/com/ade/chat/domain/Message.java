package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "message")
@Table(
        name = "messages",
        indexes = @Index(name = "chat_id_index", columnList = "chat_id")
)
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
    private Chat chat;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "messages_undelivered_to",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_id")
    )
    private Set<User> undeliveredTo = new LinkedHashSet<>();

    public void removeRecipient(User user) {
        undeliveredTo.remove(user);
        user.getUndeliveredMessages().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return id != null && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
