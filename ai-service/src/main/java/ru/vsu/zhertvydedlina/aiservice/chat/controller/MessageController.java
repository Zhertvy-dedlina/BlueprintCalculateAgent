package ru.vsu.zhertvydedlina.aiservice.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.service.MessageService;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;

@Controller
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    @Qualifier("messageServiceWithFileProcessing")
    private final MessageService messageService;

    private final MessageMapper messageMapper;

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDto> getMessageById(
            @AuthenticationPrincipal User user,
            @RequestParam("id") Long id
    ) {
        if (!messageService.checkMessageOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(messageMapper.messageToMessageResponseDto(messageService.getMessageById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDto> updateMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody MessageUpdateRequestDto message
    ) {
        if (!messageService.checkMessageOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(messageMapper.messageToMessageResponseDto(messageService.updateMessage(message)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> deleteMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        if (!messageService.checkMessageOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(messageMapper.messageToMessageResponseDto(messageService.deleteMessage(id)));
    }
}
