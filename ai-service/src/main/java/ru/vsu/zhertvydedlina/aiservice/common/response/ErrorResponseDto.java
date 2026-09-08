package ru.vsu.zhertvydedlina.aiservice.common.response;

public record ErrorResponseDto(
        int status,
        String message
) {
}
