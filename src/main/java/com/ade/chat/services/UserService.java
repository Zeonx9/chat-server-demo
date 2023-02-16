package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.exception.UserNotFoundException;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Сервис обрабатывающий запросы связанные с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    /**
     * @param id идентификатор запрашиваемого пользователя
     * @return одного пользователя по его идентификатору
     * @throws UserNotFoundException если пользователя с таким идентификатором нет
     */
    public User getUserByIdOrException(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user with given id:" + id));
    }
    /**
     * @return список всех достпупных пользователей
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @param id логин пользоваетля
     * @return список чатов, в которых состоит пользователь с указаннным идентификатором
     * @throws UserNotFoundException если не существует пользовваетля с указанным айди
     */
    public List<Chat> getUserChats(Long id) {
        return List.copyOf(getUserByIdOrException(id).getChats());
    }

    /**
     * получает список сообщений, еще не доставленных пользователю и затем помечает их, как доставленные
     * @param id идентификатор пользователя
     * @return список сообщений
     * @throws UserNotFoundException если передан неверный идентификатор пользователя
     */
    @Transactional
    public List<Message> getUndeliveredMessagesAndMarkAsDelivered(Long id) {
        System.out.println("requesting undelivered messages");
        User user = getUserByIdOrException(id);
        List<Message> messages = new ArrayList<>();
        user.getUndeliveredMessages().forEach(message -> {
            messages.add(message);
            message.getUndeliveredTo().remove(user);
        });
        user.getUndeliveredMessages().clear();
        return messages;
    }
}
