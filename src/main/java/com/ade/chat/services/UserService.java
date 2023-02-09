package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.User;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
     * @throws IllegalStateException если пользователя с таким идентификатором нет
     */
    public User getUserByIdOrException(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("No user with given id:" + id));
    }

    /**
     * @return список всех достпупных пользователей
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * сохраняет нового пользователя в базу данных
     * @param newUser новый пользователь, которого мы сохраним
     * @throws IllegalStateException если логин пользователя(имя) уже занято
     */
    public void createUser(User newUser) {
        Boolean hasSuchUser = userRepo.existsByUsername(newUser.getUsername());
        if (hasSuchUser)
            throw new IllegalStateException("Name:" + newUser.getUsername() + " is taken already");

        userRepo.save(newUser);
    }

    /**
     * возвращает пользователя по его логину или создает нового, если имя не занято
     * @param name логин пользоваетля
     * @return созданного или полученного пользователя
     */
    public User getUserByNameOrCreate(String name) {
        Optional<User> userOptional = userRepo.findByUsername(name);
        return userOptional.orElseGet(() -> userRepo.save(User.builder().username(name).build()));
    }

    /**
     * @param id логин пользоваетля
     * @return список чатов, в которых состоит пользователь с указаннным идентификатором
     * @throws IllegalStateException если не существует пользовваетля с указанным айди
     */
    public List<Chat> getUserChats(Long id) {
        return List.copyOf(getUserByIdOrException(id).getChats());
    }
}
