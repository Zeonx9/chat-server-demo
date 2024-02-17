package com.ade.chat.services;

import com.ade.chat.domain.Chat;
import com.ade.chat.domain.UnreadCounter;
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
import java.util.function.Consumer;
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
     * @return список всех доступных пользователей
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
     * @param id логин пользователя
     * @return список чатов, в которых состоит пользователь с указанным идентификатором
     * @throws UserNotFoundException если не существует пользователя с указанным айди
     */
    public List<Chat> getUserChats(Long id) {
        List<Chat> chats = new ArrayList<>(getUserByIdOrException(id).getChats());
        chats.sort(Comparator.comparing(Chat::getLastMessageTime, Comparator.reverseOrder()));
        return chats;
    }

    /**
     * Меняет поля, если новые значения заданы
     */
    public User updateUserData(Long id, UserDto newUser) {
        User user = getUserByIdOrException(id);
        setIfNotNull(newUser::getRealName, user::setRealName);
        setIfNotNull(newUser::getSurname, user::setSurname);
        setIfNotNull(newUser::getPatronymic, user::setPatronymic);
        setIfNotNull(newUser::getDateOfBirth, user::setDateOfBirth);
        setIfNotNull(newUser::getPhoneNumber, user::setPhoneNumber);
        return user;
    }

    private <T> void  setIfNotNull(Supplier<T> getter, Consumer<T> setter) {
        T newValue = getter.get();
        if (newValue == null) {
            return;
        }
        setter.accept(newValue);
    }

    /**
     * @param id идентификатор пользователя
     * @return информацию о непрочитанных сообщениях в чатах данного пользователя
     */
    public List<UnreadCounter> getChatCountersByUserId(Long id) {
        return getUserByIdOrException(id).getChatUnreadCounters().stream()
                .filter((counter) -> counter.getCount() != 0)
                .toList();
    }

    /**
     * Устанавливает пользователю статус Online
     * @param id идентификатор пользователя
     */
    public void setOnline(Long id) {
        getUserByIdOrException(id).setIsOnline(true);
    }

    /**
     * Устанавливает пользователю статус Offline
     * @param id идентификатор пользователя
     */
    public void setOffline(Long id) {
        getUserByIdOrException(id).setIsOnline(false);
    }
}
