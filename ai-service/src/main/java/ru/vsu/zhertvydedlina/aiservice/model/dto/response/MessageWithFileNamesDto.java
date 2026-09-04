package ru.vsu.zhertvydedlina.aiservice.model.dto.response;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageWithFileNamesDto(
        Long chatId,
        Long userId,
        String message,
        List<String> fileNames,
        Instant timestamp
) {
}
