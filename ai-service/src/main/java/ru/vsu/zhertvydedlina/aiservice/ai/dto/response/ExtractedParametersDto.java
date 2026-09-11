package ru.vsu.zhertvydedlina.aiservice.ai.dto.response;

import lombok.Data;
import ru.vsu.zhertvydedlina.aiservice.calculation.entity.OperationsParameters;

import java.math.BigDecimal;

@Data
public class ExtractedParametersDto {

    private String productName;

    private String material;

    private BigDecimal thickness;

    private BigDecimal width;

    private BigDecimal height;

    private Integer quantity;

    private OperationsParameters operations;


    private boolean dataSufficient;      // Хватает ли данных для расчета
    private String clarifyingQuestion;     // Вопрос пользователю, если данных не хватает
    private String rawAiResponse;          // Для отладки: сырой ответ от модели
}