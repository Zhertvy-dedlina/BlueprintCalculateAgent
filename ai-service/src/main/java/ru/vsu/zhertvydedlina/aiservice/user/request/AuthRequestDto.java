package ru.vsu.zhertvydedlina.aiservice.user.request;

import lombok.Builder;

@Builder
public record AuthRequestDto(
        String username,
        String password
) {
}
