package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.Message;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.exception.UserNotFoundException;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


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
        return userRepo.findAll(Sort.by(Sort.Direction.ASC, "username"));
    }

    /**
     * @return список пользователей из заданной компании
     */
    public List<User> getAllUsersFromCompany(Long id) {
        return userRepo.findByCompany_Id(id, Sort.by(Sort.Direction.ASC, "username"));
    }

    /**
     * @param id логин пользоваетеля
     * @return список чатов, в которых состоит пользователь с указаннным идентификатором
     * @throws UserNotFoundException если не существует пользовваетля с указанным айди
     */
    public List<Chat> getUserChats(Long id) {
        List<Chat> chats = new ArrayList<>(getUserByIdOrException(id).getChats());
        chats.sort(Comparator.comparing(Chat::getLastMessageTime, Comparator.reverseOrder()));
        return chats;
    }
    

    /**
     * Получает список сообщений, еще не доставленных пользователю и затем помечает их, как доставленные
     * @param id идентификатор пользователя
     * @return список сообщений
     * @throws UserNotFoundException если передан неверный идентификатор пользователя
     */
    public List<Message> getUndeliveredFor(Long id) {
        User user = getUserByIdOrException(id);
        return List.copyOf(user.getUndeliveredMessages());
    }

    public void markAsDelivered(List<Message> messages, Long userId) {
        User user = getUserByIdOrException(userId);
        for (Message msg : messages) {
            msg.removeRecipient(user);
        }
        messages.forEach(user.getUndeliveredMessages()::remove);
    }

    /**
     * Меняет поля, если новые значения заданы
     */
    public User updateUserData(Long id, UserDto newUser) {
        User user = getUserByIdOrException(id);
        setIfNotNull(newUser::getRealName, user::setRealName);
        setIfNotNull(newUser::getSurname, user::setSurname);
        setIfNotNull(newUser::getDateOfBirth, user::setDateOfBirth);
        return user;
    }

    private <T> void  setIfNotNull(Supplier<T> getter, Consumer<T> setter) {
        T newValue = getter.get();
        if (newValue == null) {
            return;
        }
        setter.accept(newValue);
    }
}
