package ru.vsu.zhertvydedlina.aiservice.chat.model.request;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageRequestDto(
        Long chatId,
        Long userId,
        String message,
        List<Long> filesId,
        Instant timestamp
) {
}
