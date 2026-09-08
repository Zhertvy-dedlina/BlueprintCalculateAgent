package ru.vsu.zhertvydedlina.aiservice.chat.model.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageResponseDto(
        Long id,
        Long chatId,
        Long userId,
        String message,
        List<Long> filesId,
        Instant timestamp
) {
}
