package ru.vsu.zhertvydedlina.aiservice.ai.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResponse {
    private String status; // "SUCCESS", "NEED_CLARIFICATION", "ERROR"
    private String message; // Вопрос пользователю или сообщение об ошибке
    private byte[] excelFileBytes; // Готовый файл КП
    private ExtractedParametersDto extractedData; // Для отображения пользователю того, что понял ИИ

    public static AgentResponse needClarification(String question, ExtractedParametersDto data) {
        return AgentResponse.builder()
                .status("NEED_CLARIFICATION")
                .message(question)
                .extractedData(data)
                .build();
    }

    public static AgentResponse success(byte[] fileBytes, ExtractedParametersDto data) {
        return AgentResponse.builder()
                .status("SUCCESS")
                .message("Расчет успешно выполнен. Коммерческое предложение сформировано.")
                .excelFileBytes(fileBytes)
                .extractedData(data)
                .build();
    }

    public static AgentResponse error(String msg) {
        return AgentResponse.builder().status("ERROR").message(msg).build();
    }
}