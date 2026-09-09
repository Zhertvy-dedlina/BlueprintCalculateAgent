package ru.vsu.zhertvydedlina.aiservice.user.model.request;

public record RegisterRequestDto(
        String username,
        String email,
        String password
) {
}
