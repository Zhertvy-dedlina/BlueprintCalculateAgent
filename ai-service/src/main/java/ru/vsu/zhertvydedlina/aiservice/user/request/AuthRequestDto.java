package ru.vsu.zhertvydedlina.aiservice.user.request;

import lombok.Builder;

@Builder
public record AuthRequestDto(
        String email,
        String password
) {
}
