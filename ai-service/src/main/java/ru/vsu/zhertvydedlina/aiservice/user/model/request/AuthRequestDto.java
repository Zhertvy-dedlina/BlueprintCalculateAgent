package ru.vsu.zhertvydedlina.aiservice.user.model.request;

import lombok.Builder;

@Builder
public record AuthRequestDto(
        String username,
        String password
) {
}
