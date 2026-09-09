package ru.vsu.zhertvydedlina.aiservice.user.model.request;

import lombok.Builder;

@Builder
public record UserUpdateRequestDto(
        String username,
        String email
) {
}
