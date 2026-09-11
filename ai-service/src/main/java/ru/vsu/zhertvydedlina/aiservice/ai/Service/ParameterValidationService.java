package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.*;
import ru.vsu.zhertvydedlina.aiservice.calculation.entity.*;

@Service
public class ParameterValidationService {

    public ValidationResult validate(ExtractedParametersDto params) {

        if (params == null) {
            return ValidationResult.invalid(
                    "Не удалось извлечь параметры из заявки."
            );
        }

        if (isBlank(params.getMaterial())) {
            return ValidationResult.invalid(
                    "Укажите материал детали."
            );
        }

        if (params.getThickness() == null) {
            return ValidationResult.invalid(
                    "Укажите толщину металла."
            );
        }

        if (params.getQuantity() == null ||
                params.getQuantity() <= 0) {

            return ValidationResult.invalid(
                    "Укажите количество деталей."
            );
        }

        OperationsParameters operations =
                params.getOperations();

        if (operations == null) {
            return ValidationResult.invalid(
                    "Не удалось определить операции обработки."
            );
        }

        /*
         * Проверяем только те операции,
         * которые реально указаны.
         */

        if (operations.getLaser() != null) {

            LaserCuttingParameters laser =
                    operations.getLaser();

            if (laser.getCuttingLength() == null) {
                return ValidationResult.invalid(
                        "Для расчёта лазерной резки " +
                                "укажите общую длину реза одной детали в мм."
                );
            }
        }

        if (operations.getBending() != null) {

            BendingParameters bending =
                    operations.getBending();

            if (bending.getBendCount() == null) {
                return ValidationResult.invalid(
                        "Для расчёта гибки укажите количество гибов."
                );
            }
        }

        if (operations.getWelding() != null) {

            WeldingParameters welding =
                    operations.getWelding();

            if (welding.getWeldingLength() == null) {
                return ValidationResult.invalid(
                        "Для расчёта сварки укажите общую длину сварного шва."
                );
            }
        }

        if (operations.getPainting() != null) {

            PaintingParameters painting =
                    operations.getPainting();

            if (painting.getAreaM2() == null) {
                return ValidationResult.invalid(
                        "Для расчёта порошковой покраски " +
                                "укажите площадь окрашивания одной детали в м²."
                );
            }
        }

        return ValidationResult.valid();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}