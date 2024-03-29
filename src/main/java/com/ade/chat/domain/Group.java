package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Сущность описвыющая групповой чат, личные чаты не имеют связи с такой сущностью
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "group")
@Table(name = "groups")
public class Group {
    @Id
    @SequenceGenerator(name = "group_seq", sequenceName = "group_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "name")
    private String name;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Group group = (Group) o;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}