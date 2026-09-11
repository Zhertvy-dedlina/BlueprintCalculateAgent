package ru.vsu.zhertvydedlina.aiservice.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;
    private String question;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String question) {
        return new ValidationResult(false, question);
    }
}