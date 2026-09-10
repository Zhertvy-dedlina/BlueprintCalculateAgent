package ru.vsu.zhertvydedlina.aiservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.service.UserChatService;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(ContainersConfig.class)
public class ChatUserAndFileWorkTest {

    @Autowired
    public UserChatService userChatService;

    @Autowired
    public BlueprintFileService blueprintFileService;

    @Test
    @DisplayName("Проверка работы UserChatService")
    public void userChatServiceTest() throws Exception {
        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .message("ПРИВЕТ!")
                .filesId(null)
                .userId(1L)
                .build();

        ChatWithMessagesResponseDto chatWithMessage = userChatService.createChatFromFirstMessage(messageRequestDto);

        System.out.println(chatWithMessage.toString());
    }
}
