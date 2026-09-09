package ru.vsu.zhertvydedlina.aiservice.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.repository.BlueprintFileRepository;
import ru.vsu.zhertvydedlina.aiservice.file.service.storage.FileStorageService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlueprintFileServiceImpl implements BlueprintFileService {

    private final BlueprintFileRepository blueprintFileRepository;
    private final MessageRepository messageRepository;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;

    @Override
    public BlueprintFile getBlueprintFileById(Long id) {
        return blueprintFileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Blueprint file with id " + id + " not found"));
    }

    @Override
    public List<BlueprintFile> getBlueprintFilesByChatId(Long chatId) {
        List<Long> filesId = messageRepository.findAllByChatId(chatId, Pageable.unpaged()).stream()
                .flatMap(message -> message.getFilesId() == null
                        ? java.util.stream.Stream.<Long>empty()
                        : message.getFilesId().stream())
                .distinct()
                .toList();

        return blueprintFileRepository.findAllByIdIn(filesId);
    }

    @Override
    public InputStreamResource loadBlueprintFileById(Long id) {
        BlueprintFile file = getBlueprintFileById(id);

        return new InputStreamResource(fileStorageService.get(file.getStorageKey()));
    }

    @Override
    @Transactional
    public List<BlueprintFile> uploadBlueprintFile(List<MultipartFile> files, Long userId) {
        return files.stream()
                .map(file -> uploadSingleFile(file, userId))
                .toList();
    }

    private BlueprintFile uploadSingleFile(MultipartFile file, Long userId) {
        fileValidationService.validate(file);

        String originalFileName = file.getOriginalFilename();
        String storageKey =
                "users/" + userId +
                        "/blueprints/" + UUID.randomUUID() +
                        "/" + originalFileName;

        fileStorageService.save(file, storageKey);

        try {
            BlueprintFile blueprintFile = new BlueprintFile();

            blueprintFile.setUserId(userId);
            blueprintFile.setFileName(originalFileName);
            blueprintFile.setContentType(file.getContentType());
            blueprintFile.setFileSize(file.getSize());
            blueprintFile.setStorageKey(storageKey);
            blueprintFile.setConfirmed(false);

            return blueprintFileRepository.save(blueprintFile);
        } catch (Exception e) {
            //если запись в БД не создалась, грустим, плачем и удаляем
            fileStorageService.delete(storageKey);

            throw e;
        }
    }

    @Override
    @Transactional
    public BlueprintFile deleteBlueprintFile(Long id) {
        BlueprintFile file = getBlueprintFileById(id);

        fileStorageService.delete(file.getStorageKey());
        blueprintFileRepository.delete(file);

        return file;
    }

    @Override
    @Transactional
    public List<BlueprintFile> deleteBlueprintFilesById(List<Long> filesId) {
        if (filesId == null || filesId.isEmpty()) {
            return List.of();
        }

        List<BlueprintFile> files = blueprintFileRepository.findAllByIdIn(filesId);
        files.forEach(file -> fileStorageService.delete(file.getStorageKey()));
        blueprintFileRepository.deleteAll(files);

        return files;
    }

    @Override
    @Transactional
    public BlueprintFile deleteBlueprintFileByMessageId(Long messageId) {
        List<BlueprintFile> files = blueprintFileRepository.findAllByMessageId(messageId);
        files.forEach(file -> fileStorageService.delete(file.getStorageKey()));
        blueprintFileRepository.deleteAll(files);

        return files.isEmpty() ? null : files.getFirst();
    }

    @Override
    @Transactional
    public List<BlueprintFile> confirmBlueprintFilesById(List<Long> filesId) {
        if (filesId == null || filesId.isEmpty()) {
            return List.of();
        }

        List<BlueprintFile> files = blueprintFileRepository.findAllByIdIn(filesId);
        files.forEach(file -> file.setConfirmed(true));

        return blueprintFileRepository.saveAll(files);
    }

    @Override
    public boolean checkBlueprintFilesOwner(Long messageId, List<Long> filesId) {
        if (filesId == null || filesId.isEmpty()) {
            return true;
        }

        return blueprintFileRepository.countByIdInAndMessageId(filesId, messageId) == filesId.size();
    }

    @Override
    public boolean checkBlueprintFileOwner(Long fileId, Long userId) {
        return blueprintFileRepository.existsByIdAndUserId(fileId, userId);
    }
}
