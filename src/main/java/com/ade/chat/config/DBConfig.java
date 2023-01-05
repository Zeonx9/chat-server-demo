package com.ade.chat.config;

import com.ade.chat.entities.Chat;
import com.ade.chat.entities.Message;
import com.ade.chat.entities.User;
import com.ade.chat.repositories.ChatRepository;
import com.ade.chat.repositories.MessageRepository;
import com.ade.chat.repositories.UserRepository;
import com.ade.chat.services.ChatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class DBConfig {

    @Bean
    CommandLineRunner commandLineRunner1(UserRepository userRepo,
                                         ChatRepository chatRepo,
                                         MessageRepository messageRepo) {
        return args -> {

            User u1 =  new User("Artem"), u2 =  new User("Egor"), u3 =  new User("Dasha");
            userRepo.saveAll(
                    List.of(u1, u2, u3)
            );

            Chat c1 = new Chat(true, Set.of(u1, u2)),  c2 = new Chat(true, Set.of(u3, u2)),
                    c3 = new Chat(true, Set.of(u1, u3)),  c4 = new Chat(false, Set.of(u1, u2, u3));

            chatRepo.saveAll(List.of(c1, c2, c3, c4));

            messageRepo.save(new Message(1L, "Hello, World!", LocalDateTime.now(), u1, c1));
        };
    }
}
