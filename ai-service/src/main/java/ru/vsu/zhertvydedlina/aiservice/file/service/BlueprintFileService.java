package ru.vsu.zhertvydedlina.aiservice.file.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;

import java.util.List;

@Service
public interface BlueprintFileService {
    BlueprintFile getBlueprintFileById(Long id);

    List<BlueprintFile> getBlueprintFilesByChatId(Long chatId);

    InputStreamResource loadBlueprintFileById(Long id);

    List<BlueprintFile> uploadBlueprintFile(List<MultipartFile> message);

    BlueprintFile deleteBlueprintFile(Long id);

    List<BlueprintFile> deleteBlueprintFilesById(List<Long> filesId);

    BlueprintFile deleteBlueprintFileByMessageId(Long messageId);

    List<BlueprintFile> confirmBlueprintFilesById(List<Long> filesId);

    boolean checkBlueprintFilesOwner(Long messageId, List<Long> filesId);
}
