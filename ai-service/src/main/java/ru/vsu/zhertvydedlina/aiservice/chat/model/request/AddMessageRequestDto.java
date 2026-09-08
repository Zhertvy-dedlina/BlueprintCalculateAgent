package ru.vsu.zhertvydedlina.aiservice.chat.model.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Builder
public record AddMessageRequestDto(
        String message,
        List<MultipartFile> files,
        Instant timestamp
) {
}
