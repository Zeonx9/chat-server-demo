package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Сущность чат. Хранит информацию о самом чате, позволяет получить доступ к сообщениям и пользователям из этого чата
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "chat")
@Table(name = "chats")
public class Chat {
    @Id
    @SequenceGenerator(name = "chat_sequence", sequenceName = "chat_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "chat_sequence")
    private Long id;

    @Column(name = "is_private")
    @Builder.Default
    private Boolean isPrivate = false;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.DETACH
            }
    )
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> members = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "chat")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Group group;

    @ToString.Exclude
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    public LocalDateTime getLastMessageTime() {
        return lastMessage != null ? lastMessage.getDateTime() : LocalDateTime.MIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Chat chat = (Chat) o;
        return id != null && Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
