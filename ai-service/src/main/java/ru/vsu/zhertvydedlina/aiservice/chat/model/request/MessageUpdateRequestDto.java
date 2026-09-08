package ru.vsu.zhertvydedlina.aiservice.chat.model.request;

import java.util.List;

public record MessageUpdateRequestDto(
        Long id,
        String message,
        List<Long> filesId
) {
}
