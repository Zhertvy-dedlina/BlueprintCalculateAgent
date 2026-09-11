package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import chat.giga.client.GigaChatClient;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.ExtractedParametersDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GigaChatAiService implements AiService {

    private final GigaChatClient gigaChatClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
        Ты являешься инженером-технологом металлообрабатывающего производства.
        
        Твоя задача — проанализировать текст заявки и приложенные чертежи.
        
        НЕ рассчитывай стоимость изделия.
        НЕ придумывай отсутствующие параметры.
        
        Извлеки только те параметры, которые явно указаны на чертеже,
        эскизе или в тексте пользователя.
        
        Особенно ищи технологические параметры:
        
        1. Материал:
           - марка стали;
           - нержавеющая сталь;
           - оцинкованная сталь;
           - алюминий и т.д.
        
        2. Толщина металла.
        
        3. Количество деталей.
        
        4. Лазерная/плазменная/газовая резка:
           - общая длина реза;
           - количество отверстий;
           - размеры отверстий;
           - другие параметры, влияющие на время обработки.
        
        5. Гибка:
           - количество гибов;
           - особенности гибки, если они указаны.
        
        6. Сварка:
           - длина сварных швов;
           - количество швов;
           - тип сварки, если указан.
        
        7. Покраска:
           - площадь окрашивания;
           - тип покрытия;
           - RAL;
           - количество деталей.
        
        8. Токарная обработка:
           - количество деталей;
           - время обработки, если явно указано;
           - описание операции.
        
        9. Геометрические размеры детали:
           - длина;
           - ширина;
           - высота;
           - диаметр и т.д.
        
        Размеры сами по себе НЕ считаются достаточными параметрами
        для определения стоимости сложной детали.
        
        Если параметр нельзя достоверно определить из чертежа,
        укажи null.
        
        Верни ТОЛЬКО JSON следующего формата:
        
        {
          "productName": null,
          "material": null,
          "thickness": null,
          "width": null,
          "height": null,
          "length": null,
          "quantity": null,
        
          "operations": {
            "laser": {
              "cuttingLengthMm": null,
              "holes": null,
              "quantity": null
            },
            "bending": {
              "bends": null,
              "quantity": null
            },
            "welding": {
              "seamLengthMm": null,
              "seams": null,
              "quantity": null
            },
            "painting": {
              "areaM2": null,
              "quantity": null,
              "coating": null,
              "ral": null
            },
            "turning": {
              "quantity": null,
              "machineHours": null,
              "description": null
            }
          }
        }
        """;


    @Override
    public String ask(String text) {

        CompletionRequest request = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER)
                                .content(text)
                                .build()
                )
                .build();

        var response = gigaChatClient.completions(request);

        return response.choices()
                .get(0)
                .message()
                .content();
    }


    @Override
    public ExtractedParametersDto extractParameters(String text, List<String> gigaChatFileIds) {
        try {
            ChatMessage.ChatMessageBuilder userMessageBuilder = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(text);

            if (gigaChatFileIds != null && !gigaChatFileIds.isEmpty()) {
                userMessageBuilder.attachments(gigaChatFileIds);
            }

            CompletionRequest request = CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_MAX_2)                                               //ВЫБОР МОДЕЛИ!!!!
                    .message(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(SYSTEM_PROMPT).build())
                    .message(userMessageBuilder.build())
                    .temperature(0.1F) // Низкая температура для строгости и детерминированности JSON
                    .build();

            var response = gigaChatClient.completions(request);
            String rawContent = response.choices().get(0).message().content();

            String cleanJson = rawContent.replaceAll("^```json\\s*", "")
                    .replaceAll("\\s*```$", "")
                    .trim();

            return objectMapper.readValue(cleanJson, ExtractedParametersDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось распарсить ответ ИИ в структурированный формат", e);
        }
    }
}