package com.ade.chat.config;

import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.services.ChatService;
import com.ade.chat.services.MessageService;
import com.ade.chat.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DBConfig {

    void configurationWhenDropCreateModeIsOn(UserService us, ChatService cs, MessageService ms) {
        List<String> names = List.of("Artem", "Egor", "Dasha");
                names.forEach(name -> us.createUser(new User(name)));

                List<List<Long>> chatIds = List.of(
                        List.of(1L, 2L),
                        List.of(2L, 3L),
                        List.of(1L, 3L),
                        List.of(1L, 2L, 3L)
                );
                chatIds.forEach(ids -> cs.createOrGetChat(ids, ids.size() == 2));

                ms.sendPrivateMessage(1L, 2L, new Message("Hello"));
                ms.sendPrivateMessage(2L, 1L, new Message("Oh, Hi!"));
    }

    @Bean
    CommandLineRunner commandLineRunner1() {
          return args -> {};
    }
}
