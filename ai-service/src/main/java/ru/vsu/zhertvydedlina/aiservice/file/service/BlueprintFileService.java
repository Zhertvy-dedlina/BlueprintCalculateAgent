package ru.vsu.zhertvydedlina.aiservice.file.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileWithDataDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

@Service
public interface BlueprintFileService {
    BlueprintFile getBlueprintFileById(Long id);
    BlueprintFile getBlueprintFileByName(String name);

    List<BlueprintFile> getBlueprintFilesByChatId(Long chatId);

    FileWithDataDto loadBlueprintFileById(Long id);
    FileWithDataDto loadBlueprintFileByName(String name);

    List<FileResponseDto> uploadBlueprintFile(MessageRequestDto message);

    FileResponseDto deleteBlueprintFile(Long id);
}
