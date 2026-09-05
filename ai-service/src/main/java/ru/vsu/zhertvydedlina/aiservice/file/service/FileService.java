package ru.vsu.zhertvydedlina.aiservice.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.file.dto.response.FileResponseDto;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.repository.BlueprintFileRepository;
import ru.vsu.zhertvydedlina.aiservice.file.service.storage.FileStorageService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final BlueprintFileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;

    //todo: проверка существование chatId
    public FileResponseDto upload(
            Long chatId,
            MultipartFile file
    ) {

        fileValidationService.validate(file);

        String originalFileName = file.getOriginalFilename();

        String storageKey =
                "chats/" + chatId +
                        "/files/" + UUID.randomUUID() +
                        "/" + originalFileName;

        fileStorageService.save(file, storageKey);

        try {

            BlueprintFile blueprintFile = new BlueprintFile();

            blueprintFile.setChatId(chatId);
            blueprintFile.setFileName(originalFileName);
            blueprintFile.setContentType(file.getContentType());
            blueprintFile.setFileSize(file.getSize());
            blueprintFile.setStorageKey(storageKey);

            BlueprintFile savedFile =
                    fileRepository.save(blueprintFile);

            return FileResponseDto.builder()
                    .id(savedFile.getId())
                    .chatId(savedFile.getChatId())
                    .fileName(savedFile.getFileName())
                    .contentType(savedFile.getContentType())
                    .fileSize(savedFile.getFileSize())
                    .build();

        } catch (Exception e) {
            //если запись в БД не создалась, грустим, плачем и удаляем
            fileStorageService.delete(storageKey);

            throw e;
        }
    }

}
