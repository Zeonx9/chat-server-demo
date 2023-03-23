package com.ade.chat.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "company")
@Table(name = "company")
public class Company {
    @Id
    @SequenceGenerator(name = "company_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "company", orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private Set<User> employees = new LinkedHashSet<>();
}