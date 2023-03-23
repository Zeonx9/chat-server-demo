package com.ade.chat.controllers;

import com.ade.chat.dtos.ChatDto;
import com.ade.chat.dtos.GroupRequest;
import com.ade.chat.dtos.MessageDto;
import com.ade.chat.mappers.ChatMapper;
import com.ade.chat.mappers.GroupMapper;
import com.ade.chat.mappers.MessageMapper;
import com.ade.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring REST контроллер, отвечающий за операции с чатами
 */
@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final GroupMapper groupMapper;

    /**
     * GET запрос, который по заданным ID пользователей находит существующий диалог
     * или создает новый в случае отсутствия.
     * @param id1 идентификатор одного пользователя
     * @param id2 идентификатор другого пользователя
     * @return объект диалога
     */
    @GetMapping("/private_chat/{id1}/{id2}")
    public ResponseEntity<ChatDto> getOrCreatePrivateChat(
            @PathVariable Long id1,
            @PathVariable Long id2
    ) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.createOrGetPrivateChat(id1, id2)));
    }

    /**
     * POST запрос, который по создает чат по списку идентификаторов пользователей входящих в него,
     * а так же дополнительной информации описываемой в объекте GroupDto
     * @param groupRequest DTO, которое несет информацию о группе для создания
     * @return созданный объект чата
     */
    @PostMapping("group_chat")
    public ResponseEntity<ChatDto> createGroupChat(@RequestBody GroupRequest groupRequest) {
        return ResponseEntity.ok(chatMapper.toDto(
                chatService.createGroupChat(
                        groupRequest.getIds(),
                        groupMapper.toEntity(groupRequest.getGroupInfo())
                )
        ));
    }

    /**
     * GET реквест с полным путем chat_api/v1/chats/{chatId}/messages
     * получает список сообщений из указанного чата
     * @param chatId идентификатор чата
     * @return список сообщений чата
     * @throws com.ade.chat.exception.ChatNotFoundException если нет чата с таким айди
     */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(messageMapper.toDtoList(chatService.getMessages(chatId, userId)));
    }

    /**
     * POST реквест с полным путем chat_api/v1/chat
     * @param ids список ID пользователей, которые будут участниками чата
     *            для приватных чатов обязательно только 2 ID в списке, иначе ошибка
     * @param isPrivate если true, то будет создан приватный чат, в противном случае беседа
     */
    @Deprecated
    @PostMapping("/chat")
    public ResponseEntity<ChatDto> createOrGetChat(
            @RequestBody List<Long> ids,
            @RequestParam(required = false) Boolean isPrivate) {
        return ResponseEntity.ok(chatMapper.toDto(chatService.createOrGetChat(ids, isPrivate)));
    }
}
