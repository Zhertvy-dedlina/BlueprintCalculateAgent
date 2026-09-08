package ru.vsu.zhertvydedlina.aiservice.user.response;

import lombok.Builder;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email
) {
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
