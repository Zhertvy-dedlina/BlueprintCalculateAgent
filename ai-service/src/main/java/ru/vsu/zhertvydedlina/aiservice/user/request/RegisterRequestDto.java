package ru.vsu.zhertvydedlina.aiservice.user.request;

public record RegisterRequestDto(
        String username,
        String email,
        String password
) {
}
