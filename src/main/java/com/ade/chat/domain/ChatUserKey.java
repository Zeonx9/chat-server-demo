package com.ade.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatUserKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "chat_id")
    private Long chatId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ChatUserKey that = (ChatUserKey) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getChatId() != null && Objects.equals(getChatId(), that.getChatId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(userId, chatId);
    }
}
