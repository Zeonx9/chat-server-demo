package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "group")
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

}