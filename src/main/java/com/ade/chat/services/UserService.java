package com.ade.chat.services;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.Chat;
import com.ade.chat.domain.UnreadCounter;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.UserDto;
import com.ade.chat.exception.UploadFailedException;
import com.ade.chat.exception.UserNotFoundException;
import com.ade.chat.exception.WrongAuthHeaderException;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
public class UserService {
    private final UserRepository userRepo;
    private final MinioService minioService;
    private final JwtService jwtService;

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

    public User uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = getUserByIdOrException(userId);
        String photoId = minioService.uploadFile(file);
        user.setProfilePhotoId(photoId);

        String thumbnailId = saveThumbnailOf(file);
        user.setThumbnailPhotoId(thumbnailId);

        return userRepo.save(user);
    }

    private String saveThumbnailOf(MultipartFile file) {
        byte [] thumbnailBytes;
        try {
            thumbnailBytes = createThumbnail(file.getInputStream(), "JPEG");
        }
        catch (IOException e) {
            throw new UploadFailedException("Error during thumbnail creation");
        }

        return minioService.uploadFile(
                "image/jpeg",
                thumbnailBytes.length,
                new ByteArrayInputStream(thumbnailBytes)
        );
    }

    private byte[] createThumbnail(InputStream originalBytes, String format) throws IOException {
        final int THUMBNAIL_SIZE = 96;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(originalBytes)
                .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .outputFormat(format)
                .outputQuality(1)
                .toOutputStream(out);
        return out.toByteArray();
    }

    /**
     * Получает юзера по переданному заголовку авторизации
     * @param authHeaderValue заголовок из запроса
     * @return авторизованного пользователя
     */
    public User getUserFromToken(String authHeaderValue) {
        if (authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
            throw new WrongAuthHeaderException("wrong authorization header used: should use 'Bearer <token>'");
        }
        String token = authHeaderValue.substring(7);
        String username = jwtService.extractUsername(token);

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user with username:" + username));
    }
}
