package ru.vsu.zhertvydedlina.aiservice.chat.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageWithFileNamesResponseDto(
        Long chatId,
        Long userId,
        String message,
        List<String> fileNames,
        Instant timestamp
) {
}
