package ru.vsu.zhertvydedlina.aiservice.user.model.response;

import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email
) {
}
