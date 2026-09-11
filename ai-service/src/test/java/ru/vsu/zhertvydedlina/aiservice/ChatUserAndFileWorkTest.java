package ru.vsu.zhertvydedlina.aiservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.service.UserChatService;
import ru.vsu.zhertvydedlina.aiservice.file.entity.BlueprintFile;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;
import ru.vsu.zhertvydedlina.aiservice.file.service.storage.FileStorageService;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChatUserAndFileWorkTest {

    @Autowired
    public UserChatService userChatService;

    @Autowired
    public BlueprintFileService blueprintFileService;

    @MockitoBean
    public FileStorageService fileStorageService;

    @Test
    @DisplayName("Проверка работы UserChatService")
    public void userChatServiceTest() {
        MessageRequestDto expectedMessage = MessageRequestDto.builder()
                .message("ПРИВЕТ!")
                .filesId(null)
                .userId(1L)
                .build();

        ChatWithMessagesResponseDto actualChat = userChatService.createChatFromFirstMessage(expectedMessage);

        Assertions.assertEquals(expectedMessage.message(), actualChat.messages().getFirst().message());
    }

    @Test
    @DisplayName("Проверка сохранения Файла")
    public void fileServiceTest() {
        List<MultipartFile> files = List.of(
                new MockMultipartFile(
                        "file",
                        "test.pdf",
                        "application/pdf",
                        "ПРИВЕТ!".getBytes()
                )
        );

        List<BlueprintFile> actualFiles = blueprintFileService.uploadBlueprintFile(files, 1L);

        BlueprintFile expectedFiles = blueprintFileService.getBlueprintFileById(1L);

        Assertions.assertEquals(expectedFiles.getId(), actualFiles.getFirst().getId());
    }

    @Test
    @DisplayName("Проверка подтверждения файла внутри сообщения")
    public void confirmFileTest() {
        List<MultipartFile> files = List.of(
                new MockMultipartFile(
                        "file",
                        "test.pdf",
                        "application/pdf",
                        "ПРИВЕТ!".getBytes()
                )
        );

        List<BlueprintFile> savedFiles = blueprintFileService.uploadBlueprintFile(files, 1L);

        MessageRequestDto expectedMessage = MessageRequestDto.builder()
                .message("ПРИВЕТ!")
                .filesId(List.of(savedFiles.getFirst().getId()))
                .userId(1L)
                .build();

        userChatService.createChatFromFirstMessage(expectedMessage);

        boolean expectedFileConfirm = true;

        BlueprintFile actualFile = blueprintFileService.getBlueprintFileById(savedFiles.getFirst().getId());

        System.out.println(actualFile);

        Assertions.assertEquals(expectedFileConfirm, actualFile.getConfirmed());
    }
}
