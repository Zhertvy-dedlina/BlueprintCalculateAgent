package ru.vsu.zhertvydedlina.aiservice.file.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Builder
public record FileWithDataDto(
        Long id,
        Long chatId,
        String fileName,
        String contentType,
        Long fileSize,
        InputStream fileData
) {
}
