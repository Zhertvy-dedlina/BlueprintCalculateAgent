package ru.vsu.zhertvydedlina.aiservice.chat.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.MessageWithFileNamesResponseDto;

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
}
