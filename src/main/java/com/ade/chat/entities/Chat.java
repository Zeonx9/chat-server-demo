package com.ade.chat.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity(name = "chat")
@Table(name = "chats")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Chat {
    @Id
    @SequenceGenerator(name = "chat_sequence", sequenceName = "chat_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "chat_sequence")
    private Long id;

    @Column(name = "is_private")
    private Boolean isPrivate;


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
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "chat")
    @JsonIgnore
    private List<Message> messages;

    public Chat(Boolean isPrivate, Set<User> members) {
        this.isPrivate = isPrivate;
        this.members = members;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", isPrivate=" + isPrivate +
                ", members=" + members +
                '}';
    }
}
