package ru.vsu.zhertvydedlina.aiservice.file.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.repository.BlueprintFileRepository;
import ru.vsu.zhertvydedlina.aiservice.file.service.storage.FileStorageService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlueprintFileServiceTest {

    @Mock
    private BlueprintFileRepository blueprintFileRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileValidationService fileValidationService;

    @InjectMocks
    private BlueprintFileServiceImpl blueprintFileService;

    private BlueprintFile buildFile(Long id) {
        BlueprintFile file = new BlueprintFile();
        file.setId(id);
        file.setUserId(10L);
        file.setFileName("plan.pdf");
        file.setContentType("application/pdf");
        file.setFileSize(1024L);
        file.setStorageKey("users/10/blueprints/plan.pdf");
        file.setConfirmed(false);
        return file;
    }

    @Test
    @DisplayName("Проверка получения файла по id")
    void shouldReturnFileById() {
        BlueprintFile expectedFile = buildFile(1L);
        when(blueprintFileRepository.findById(1L)).thenReturn(Optional.of(expectedFile));

        BlueprintFile actualFile = blueprintFileService.getBlueprintFileById(1L);

        Assertions.assertEquals(expectedFile.getId(), actualFile.getId());
        verify(blueprintFileRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка выброса исключения при отсутствии файла")
    void shouldThrowExceptionWhenFileNotFound() {
        when(blueprintFileRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> blueprintFileService.getBlueprintFileById(1L));
        verify(blueprintFileRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка получения файлов по id чата")
    void shouldReturnFilesByChatId() {
        Message message = new Message(1L, 5L, 10L, List.of(1L, 2L), "hello", Instant.now());
        when(messageRepository.findAllByChatId(5L, Pageable.unpaged())).thenReturn(List.of(message));

        List<BlueprintFile> expectedFiles = List.of(buildFile(1L), buildFile(2L));
        when(blueprintFileRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(expectedFiles);

        List<BlueprintFile> actualFiles = blueprintFileService.getBlueprintFilesByChatId(5L);

        Assertions.assertEquals(expectedFiles.size(), actualFiles.size());
        verify(messageRepository, times(1)).findAllByChatId(5L, Pageable.unpaged());
        verify(blueprintFileRepository, times(1)).findAllByIdIn(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Проверка удаления файла по id")
    void shouldDeleteFileById() {
        BlueprintFile expectedFile = buildFile(1L);
        when(blueprintFileRepository.findById(1L)).thenReturn(Optional.of(expectedFile));

        BlueprintFile actualFile = blueprintFileService.deleteBlueprintFile(1L);

        Assertions.assertEquals(expectedFile.getId(), actualFile.getId());
        verify(fileStorageService, times(1)).delete(expectedFile.getStorageKey());
        verify(blueprintFileRepository, times(1)).delete(expectedFile);
    }

    @Test
    @DisplayName("Проверка удаления файлов по списку id")
    void shouldDeleteFilesByIds() {
        List<BlueprintFile> expectedFiles = List.of(buildFile(1L), buildFile(2L));
        when(blueprintFileRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(expectedFiles);

        List<BlueprintFile> actualFiles = blueprintFileService.deleteBlueprintFilesById(List.of(1L, 2L));

        Assertions.assertEquals(expectedFiles.size(), actualFiles.size());
        verify(fileStorageService, times(2)).delete(anyString());
        verify(blueprintFileRepository, times(1)).deleteAll(expectedFiles);
    }

    @Test
    @DisplayName("Проверка удаления при пустом списке id возвращает пустой список")
    void shouldReturnEmptyListWhenDeletingWithEmptyIds() {
        List<BlueprintFile> actualFiles = blueprintFileService.deleteBlueprintFilesById(List.of());

        Assertions.assertTrue(actualFiles.isEmpty());
        verifyNoInteractions(blueprintFileRepository);
    }

    @Test
    @DisplayName("Проверка подтверждения файлов по списку id")
    void shouldConfirmFilesByIds() {
        List<BlueprintFile> files = List.of(buildFile(1L), buildFile(2L));
        when(blueprintFileRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(files);
        when(blueprintFileRepository.saveAll(files)).thenReturn(files);

        List<BlueprintFile> actualFiles = blueprintFileService.confirmBlueprintFilesById(List.of(1L, 2L));

        Assertions.assertTrue(actualFiles.stream().allMatch(BlueprintFile::getConfirmed));
        verify(blueprintFileRepository, times(1)).saveAll(files);
    }

    @Test
    @DisplayName("Проверка владельца файлов сообщения")
    void shouldCheckBlueprintFilesOwner() {
        when(blueprintFileRepository.countByIdInAndMessageId(List.of(1L, 2L), 5L)).thenReturn(2L);

        Assertions.assertTrue(blueprintFileService.checkBlueprintFilesOwner(5L, List.of(1L, 2L)));
        verify(blueprintFileRepository, times(1)).countByIdInAndMessageId(List.of(1L, 2L), 5L);
    }

    @Test
    @DisplayName("Проверка владельца файлов при пустом списке id возвращает true")
    void shouldReturnTrueWhenCheckingOwnerWithEmptyIds() {
        Assertions.assertTrue(blueprintFileService.checkBlueprintFilesOwner(5L, List.of()));
        verifyNoInteractions(blueprintFileRepository);
    }

    @Test
    @DisplayName("Проверка владельца файла")
    void shouldCheckBlueprintFileOwner() {
        when(blueprintFileRepository.existsByIdAndUserId(1L, 10L)).thenReturn(true);
        when(blueprintFileRepository.existsByIdAndUserId(1L, 20L)).thenReturn(false);

        Assertions.assertTrue(blueprintFileService.checkBlueprintFileOwner(1L, 10L));
        Assertions.assertFalse(blueprintFileService.checkBlueprintFileOwner(1L, 20L));
        verify(blueprintFileRepository, times(1)).existsByIdAndUserId(1L, 10L);
        verify(blueprintFileRepository, times(1)).existsByIdAndUserId(1L, 20L);
    }
}
