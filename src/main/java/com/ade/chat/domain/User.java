package com.ade.chat.domain;

import com.ade.chat.auth.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Сущность пользователя, наверное главная сущность в приложении, через нее происходит всякое взаимодействие
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity(name = "user")
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "user_sequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @ManyToMany(mappedBy = "members", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    private Set<Chat> chats = new LinkedHashSet<>();

    @Column(name = "real_name")
    private String realName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "patronymic")
    private String patronymic;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<UnreadCounter> chatUnreadCounters = new HashSet<>();

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + username + "'}";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
