package ru.vsu.zhertvydedlina.aiservice.chat;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.zhertvydedlina.aiservice.chat.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

@Mapper(componentModel = "spring", imports = BlueprintFile.class)
public interface MessageMapping {
    @Mapping(target = "fileNames", ignore = true)
    MessageWithFileNamesResponseDto messageToMessageWithoutFiles(Message message);

    @Mapping(target = "fileNames", expression = "java(file.stream().map(BlueprintFile::getFileName).toList())")
    MessageWithFileNamesResponseDto messageToMessageWithoutFiles(Message message, List<BlueprintFile> files);

    default ChatWithMessagesResponseDto messageToChatWithMessages(Chat chat, List<Message> messages) {
        return ChatWithMessagesResponseDto.builder()
                .chatId(chat.getId())
                .userId(chat.getUserId())
                .messages(messages.stream()
                        .map(this::messageToMessageWithoutFiles)
                        .toList())
                .build();
    }
}
