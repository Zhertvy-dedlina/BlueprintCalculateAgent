package ru.vsu.zhertvydedlina.aiservice;

import chat.giga.client.GigaChatClient;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vsu.zhertvydedlina.aiservice.component.GigaChatConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Проверка работы клиента giga chat")
    void testGigaChatClient() {
        GigaChatClient client = new GigaChatConnection().gigaChatClient();

        CompletionResponse response = client.completions(CompletionRequest.builder()
                        .model(ModelName.GIGA_CHAT_2)
                        .message(ChatMessage.builder()
                                .role(ChatMessageRole.USER)
                                .content("Я проверяю работу апи. Отправь в ответ только одно слово и больше ничего. Слово - Привет.")
                                .build())
                .build());

        String actualResult = response.choices().getFirst().message().content();
        String expectedResult = "Привет";

        assertEquals(expectedResult, actualResult);
    }

}
