package ru.vsu.zhertvydedlina.aiservice.component.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.ChatWithMessagesDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.response.MessageWithFileNamesDto;
import ru.vsu.zhertvydedlina.aiservice.model.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.model.entity.Message;

import java.util.List;

@Mapper(componentModel = "spring", imports = BlueprintFile.class)
public interface MessageMapping {
    @Mapping(target = "fileNames", ignore = true)
    MessageWithFileNamesDto messageToMessageWithoutFiles(Message message);

    @Mapping(target = "fileNames", expression = "java(file.stream().map(BlueprintFile::getFileName).toList())")
    MessageWithFileNamesDto messageToMessageWithoutFiles(Message message, List<BlueprintFile> files);

    default ChatWithMessagesDto messageToChatWithMessages(Chat chat, List<Message> messages) {
        return ChatWithMessagesDto.builder()
                .chatId(chat.getId())
                .userId(chat.getUserId())
                .messages(messages.stream()
                        .map(this::messageToMessageWithoutFiles)
                        .toList())
                .build();
    }
}
