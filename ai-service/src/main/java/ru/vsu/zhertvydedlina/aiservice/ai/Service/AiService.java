package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.ExtractedParametersDto;

import java.util.List;

public interface AiService {

    String ask(String text);

    ExtractedParametersDto extractParameters(
            String text,
            List<String> gigaChatFileIds
    );
}