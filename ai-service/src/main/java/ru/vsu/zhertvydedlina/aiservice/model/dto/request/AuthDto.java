package ru.vsu.zhertvydedlina.aiservice.model.dto.request;

import lombok.Builder;

@Builder
public record AuthDto(
        String email,
        String password
) {
}
