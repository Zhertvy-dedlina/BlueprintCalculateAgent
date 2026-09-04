package ru.vsu.zhertvydedlina.aiservice.model.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatWithMessagesDto(
        Long chatId,
        Long userId,
        List<MessageWithFileNamesDto> messages
) {
}
