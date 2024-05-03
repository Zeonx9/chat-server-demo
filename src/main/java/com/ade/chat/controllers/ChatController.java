package com.ade.chat.controllers;

import com.ade.chat.dtos.ChangeGroupChatInfoRequest;
import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.GroupRequest;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.GroupMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Отвечает за операции с чатами, все методы требуют уровня доступа USER
 */
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final GroupMapper groupMapper;

    /**
     * Находит существующий диалог по заданным ID пользователей
     * или создает новый в случае отсутствия.
     * @param id1 идентификатор одного пользователя
     * @param id2 идентификатор другого пользователя
     * @return объект диалога
     */
    @GetMapping("/private_chat/{id1}/{id2}")
    @Transactional
    public ResponseEntity<ChatDto> getOrCreatePrivateChat(@PathVariable Long id1, @PathVariable Long id2) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.createOrGetPrivateChat(id1, id2)));
    }

    /**
     * Создает чат по списку идентификаторов пользователей входящих в него,
     * а так же дополнительной информации описываемой в объекте GroupDto
     * @param groupRequest DTO, которое несет информацию о группе для создания
     * @return созданный объект чата
     */
    @PostMapping("/group_chat")
    public ResponseEntity<ChatDto> createGroupChat(@RequestBody GroupRequest groupRequest) {
        return ResponseEntity.ok(chatMapper.toDto(
                chatService.createGroupChat(
                        groupRequest.getIds(),
                        groupMapper.toEntity(groupRequest.getGroupInfo())
                )
        ));
    }

    /**
     * Возвращает список сообщений из указанного чата
     * @param chatId идентификатор чата
     * @return список сообщений чата
     * @throws com.ade.chat.exception.ChatNotFoundException если нет чата с таким айди
     */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageMapper.toDtoList(chatService.getMessages(chatId)));
    }

    /**
     * Возвращает чат по его id
     * @param chatId идентификатор чата
     * @return объект чата
     * @throws com.ade.chat.exception.ChatNotFoundException если чата не существует
     */
    @GetMapping("/chats/{chatId}")
    public ResponseEntity<ChatDto> getChatById(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.getChatByIdOrException(chatId)));
    }

    /**
     * Добавляет нового пользователя в существующую беседу
     * @param chatId идентификатор чата
     * @param memberId идентификатор добавляемого пользователя
     * @param authValue токен авторизации
     * @return измененный чат
     */
    @PutMapping("/chats/{chatId}/new_member/{memberId}")
    @Transactional
    public ResponseEntity<ChatDto> addNewChatMember(
            @PathVariable Long chatId,
            @PathVariable Long memberId,
            @RequestHeader(name = "Authorization") String authValue
    ) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.addNewMember(chatId, memberId, authValue)));
    }

    /**
     * Удаляет выбранный чат, это может сделать только создатель чата
     * @param chatId идентификатор удаляемого чата
     * @param authValue токен авторизации
     * @return удаленный чат
     */
    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<ChatDto> deleteChatById(@PathVariable Long chatId, @RequestHeader(name = "Authorization") String authValue) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.deleteChatById(chatId, authValue)));
    }

    /**
     * Удаляет члена из чата, при этом он теряет историю сообщений,
     * может сделать это только создатель чата (или человек может выйти сам), создает вспомогательное сообщение об удалении,
     * посылает всем членам чата уведомление
     * @param chatId идентификатор чата
     * @param memberId идентификатор удаляемого
     * @param authValue заголовок авторизации
     * @return измененный чат
     */
    @PutMapping("/chats/{chatId}/delete_member/{memberId}")
    @Transactional
    public ResponseEntity<ChatDto> deleteChatMember(
            @PathVariable Long chatId,
            @PathVariable Long memberId,
            @RequestHeader(name = "Authorization") String authValue
    ) {
       return ResponseEntity.ok(chatMapper.toDto(chatService.deleteMember(chatId, memberId, authValue)));
    }

    /**
     * Изменяет аватарку группового чата
     * @param chatId идентификатор чата
     * @param file файл с новой аватаркой, должен быть изображением.
     * @return измененный объект чата
     */
    @PostMapping(value = "/chats/{id}/group_photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatDto> uploadGroupPhoto(
            @PathVariable("id") Long chatId,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.uploadGroupPhoto(chatId, file)));
    }

    /**
     * Изменяет название группового чата
     * @param chatId идентификатор чата
     * @param changeRequest объект, который содержит новый значения
     * @return измененный чат
     */
    @PutMapping("chats/{id}/group_name")
    @Transactional
    public ResponseEntity<ChatDto> changeGroupChatName(
            @PathVariable("id") Long chatId,
            @RequestBody ChangeGroupChatInfoRequest changeRequest
    ) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.changeGroupInfo(chatId, changeRequest)));
    }
}
