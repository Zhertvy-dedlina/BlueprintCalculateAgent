package ru.vsu.zhertvydedlina.aiservice.chat.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatWithMessagesResponseDto(
        Long chatId,
        Long userId,
        List<MessageWithFileNamesResponseDto> messages
) {
}
