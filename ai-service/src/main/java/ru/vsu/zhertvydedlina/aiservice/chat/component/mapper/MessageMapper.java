package ru.vsu.zhertvydedlina.aiservice.chat.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.AddMessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "fileNames", ignore = true)
    MessageWithFileNamesResponseDto messageToMessageWithFileNames(Message message);

    @Mapping(target = "fileNames", source = "filesId", qualifiedByName = "filesToNames")
    MessageWithFileNamesResponseDto messageToMessageWithFileNames(Message message, List<BlueprintFile> files);

    @Mapping(target = "fileNames", source = "filesId", qualifiedByName = "filesDtoToNames")
    MessageWithFileNamesResponseDto messageAndFileDtoToMessageWithFileNames(Message message, List<FileResponseDto> files);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filesId", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", defaultExpression = "java(java.time.Instant.now())")
    Message messageRequestDtoToMessage(MessageRequestDto messageRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filesId", source="filesId", qualifiedByName = "filesToIds")
    @Mapping(target = "timestamp", source = "messageRequestDto.timestamp", defaultExpression = "java(java.time.Instant.now())")
    Message messageRequestDtoToMessage(MessageRequestDto messageRequestDto, List<BlueprintFile> files);

    @Named("filesToNames")
    default List<String> filesToNames(List<BlueprintFile> files) {
        if (files == null) {
            return List.of();
        }

        return files.stream()
                .map(BlueprintFile::getFileName)
                .toList();
    }

    @Named("filesDtoToNames")
    default List<String> filesDtoToNames(List<FileResponseDto> files) {
        if (files == null) {
            return List.of();
        }

        return files.stream()
                .map(FileResponseDto::getFileName)
                .toList();
    }

    @Named("filesToIds")
    default List<Long> filesToIds(List<BlueprintFile> files) {
        if (files == null) {
            return List.of();
        }

        return files.stream()
                .map(BlueprintFile::getId)
                .toList();
    }

    @Mapping(target = "chatId", source = "chatId")
    MessageRequestDto updateChatIdInMessageRequestDto(MessageRequestDto message, Long chatId);

    @Mapping(target = "message", source = "textMessage")
    @Mapping(target = "filesId", source = "filesId")
    Message updateMessage(@MappingTarget Message message, String textMessage, List<Long> filesId);

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "userId", ignore = true)
    MessageRequestDto addMessageRequestDtoToMessageRequestDto(AddMessageRequestDto message, Long chatId);

    MessageResponseDto messageToMessageResponseDto(Message message);
}
