package ru.vsu.zhertvydedlina.aiservice.model.dto.request;

public record RegisterDto(
        String username,
        String email,
        String password
) {
}
