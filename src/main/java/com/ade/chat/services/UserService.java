package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.User;
import com.ade.chat.exception.UserNotFoundException;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
