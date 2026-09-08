package ru.vsu.zhertvydedlina.aiservice.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.AddMessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.ChatMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.service.UserChatService;
import ru.vsu.zhertvydedlina.aiservice.common.response.ErrorResponseDto;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;

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
    public ResponseEntity<?> getChat(@AuthenticationPrincipal User user, @PathVariable long id) {
        if (!userChatService.checkChatOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), CHAT_NOT_OWNED_MESSAGE));
        }

        return ResponseEntity.ok(chatMapper.chatToChatResponseDto(userChatService.getChatById(id)));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getChatWithMessages(
            @AuthenticationPrincipal User user,
            @PathVariable long id,
            @RequestParam int page,
            @RequestParam(required = false) Integer size
    ) {
        if (!userChatService.checkChatOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), CHAT_NOT_OWNED_MESSAGE));
        }

        ChatWithMessagesResponseDto chatWithMessages;

        if (size == null) {
            chatWithMessages = userChatService.getChatMessages(id, page);
        } else {
            chatWithMessages = userChatService.getChatMessages(id, page, size);
        }

        return ResponseEntity.ok(chatWithMessages);
    }

    @PostMapping("/createChat")
    public ResponseEntity<?> saveChat(
            @AuthenticationPrincipal User user,
            @RequestBody MessageRequestDto firstMessage
    ) {
        if (!Objects.equals(user.getId(), firstMessage.userId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userChatService.createChatFromFirstMessage(firstMessage));
    }

    @PostMapping("/{chatId}/addMessage")
    public ResponseEntity<?> addMessage(
            @AuthenticationPrincipal User user,
            @PathVariable long chatId,
            @RequestBody AddMessageRequestDto message
    ) {
        if (!userChatService.checkChatOwner(chatId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), CHAT_NOT_OWNED_MESSAGE)
            );
        }

        return ResponseEntity.ok(userChatService.addMessageToChat(
                chatId,
                messageMapper.addMessageRequestDtoToMessageRequestDto(message, chatId)
        ));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteChat(
            @AuthenticationPrincipal User user,
            @PathVariable long id
    ) {
        if (!userChatService.checkChatOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), CHAT_NOT_OWNED_MESSAGE));
        }

        return ResponseEntity.ok(chatMapper.chatToChatResponseDto(userChatService.deleteChat(id)));
    }
}
