package ru.vsu.zhertvydedlina.aiservice.chat.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "userId", source = "chat.userId")
    @Mapping(target = "messages", source = "messageWithFileNames")
    ChatWithMessagesResponseDto messageToChatWithMessages(
            Chat chat,
            List<MessageWithFileNamesResponseDto> messageWithFileNames
    );

    ChatResponseDto chatToChatResponseDto(Chat chat);
}
