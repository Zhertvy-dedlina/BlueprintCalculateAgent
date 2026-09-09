package ru.vsu.zhertvydedlina.aiservice.file.dto.response;

import lombok.Builder;

@Builder
public record BlueprintFileResponseDto(
        Long id,
        String fileName,
        String contentType,
        Long fileSize
) {
}
