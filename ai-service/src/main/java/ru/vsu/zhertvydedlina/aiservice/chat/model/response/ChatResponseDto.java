package ru.vsu.zhertvydedlina.aiservice.chat.model.response;

import lombok.Builder;

@Builder
public record ChatResponseDto(
        Long id,
        Long userId
) {
}
