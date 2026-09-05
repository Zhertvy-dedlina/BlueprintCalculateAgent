package ru.vsu.zhertvydedlina.aiservice.chat;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageRequestDto(
        Long chatId,
        Long userId,
        String message,
        List<MultipartFile> files,
        Instant timestamp
) {
}
