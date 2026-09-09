package ru.vsu.zhertvydedlina.aiservice.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.AddMessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.ChatMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.service.UserChatService;
import ru.vsu.zhertvydedlina.aiservice.common.exception.ForbiddenException;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;

import java.util.Objects;

@Controller
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private static final String CHAT_NOT_OWNED_MESSAGE = "Данный чат не принадлежит пользователю или не существует";

    private final UserChatService userChatService;

    private final ChatMapper chatMapper;

    private final MessageMapper messageMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ChatResponseDto> getChat(@AuthenticationPrincipal User user, @PathVariable long id) {
        requireChatOwner(id, user.getId());

        return ResponseEntity.ok(chatMapper.chatToChatResponseDto(userChatService.getChatById(id)));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ChatWithMessagesResponseDto> getChatWithMessages(
            @AuthenticationPrincipal User user,
            @PathVariable long id,
            @RequestParam int page,
            @RequestParam(required = false) Integer size
    ) {
        requireChatOwner(id, user.getId());

        ChatWithMessagesResponseDto chatWithMessages;

        if (size == null) {
            chatWithMessages = userChatService.getChatMessages(id, page);
        } else {
            chatWithMessages = userChatService.getChatMessages(id, page, size);
        }

        return ResponseEntity.ok(chatWithMessages);
    }

    @PostMapping("/createChat")
    public ResponseEntity<ChatWithMessagesResponseDto> saveChat(
            @AuthenticationPrincipal User user,
            @RequestBody MessageRequestDto firstMessage
    ) {
        if (!Objects.equals(user.getId(), firstMessage.userId())) {
            throw new ForbiddenException(CHAT_NOT_OWNED_MESSAGE);
        }

        return ResponseEntity.ok(userChatService.createChatFromFirstMessage(firstMessage));
    }

    @PostMapping("/{chatId}/addMessage")
    public ResponseEntity<MessageWithFileNamesResponseDto> addMessage(
            @AuthenticationPrincipal User user,
            @PathVariable long chatId,
            @RequestBody AddMessageRequestDto message
    ) {
        requireChatOwner(chatId, user.getId());

        return ResponseEntity.ok(userChatService.addMessageToChat(
                chatId,
                messageMapper.addMessageRequestDtoToMessageRequestDto(message, chatId)
        ));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ChatResponseDto> deleteChat(
            @AuthenticationPrincipal User user,
            @PathVariable long id
    ) {
        requireChatOwner(id, user.getId());

        return ResponseEntity.ok(chatMapper.chatToChatResponseDto(userChatService.deleteChat(id)));
    }

    private void requireChatOwner(long chatId, Long userId) {
        if (!userChatService.checkChatOwner(chatId, userId)) {
            throw new ForbiddenException(CHAT_NOT_OWNED_MESSAGE);
        }
    }
}
