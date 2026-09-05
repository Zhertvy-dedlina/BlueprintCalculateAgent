package ru.vsu.zhertvydedlina.aiservice.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.io.InputStream;
import java.util.List;

@Service
public interface BlueprintFileService {
    BlueprintFile getBlueprintFileById(Long id);
    BlueprintFile getBlueprintFileByName(String name);

    List<BlueprintFile> getBlueprintFilesByChatId(Long chatId);

    InputStream loadBlueprintFileById(Long id);
    InputStream loadBlueprintFileByName(String name);

    List<FileResponseDto> uploadBlueprintFile(MessageRequestDto message);
}
