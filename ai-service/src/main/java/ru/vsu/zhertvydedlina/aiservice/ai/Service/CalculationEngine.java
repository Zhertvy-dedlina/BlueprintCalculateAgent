package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.calculation.entity.*;
import ru.vsu.zhertvydedlina.aiservice.calculation.request.CalculationResult;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.*;
import ru.vsu.zhertvydedlina.aiservice.excel.ExcelCalculationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CalculationEngine {

    private static final BigDecimal VAT =
            new BigDecimal("0.22");

    private static final BigDecimal LASER_HOUR_PRICE =
            new BigDecimal("4800");

    private static final BigDecimal BENDING_HOUR_PRICE =
            new BigDecimal("2685");

    private static final BigDecimal TURNING_HOUR_PRICE =
            new BigDecimal("4800");

    private static final BigDecimal WELDING_HOUR_PRICE =
            new BigDecimal("1980");

    private static final BigDecimal PAINTING_HOUR_PRICE =
            new BigDecimal("2500");

    /**
     * Производительность гибки:
     * 84 гиба/час.
     */
    private static final BigDecimal BENDS_PER_HOUR =
            new BigDecimal("84");

    /**
     * Производительность порошковой покраски:
     * 5.53 м²/час.
     */
    private static final BigDecimal PAINTING_M2_PER_HOUR =
            new BigDecimal("5.53");

    private final ExcelCalculationService pricingService;

    public CalculationResult calculate(
            ExtractedParametersDto params) {

        MaterialPrice material =
                pricingService.findMaterial(
                        params.getMaterial()
                );

        if (material == null) {
            throw new IllegalArgumentException(
                    "Материал не найден в Excel: "
                            + params.getMaterial()
            );
        }

        BigDecimal materialCost =
                calculateMaterialCost(
                        params,
                        material
                );

        BigDecimal laserCost =
                calculateLaser(params);

        BigDecimal bendingCost =
                calculateBending(params);

        BigDecimal weldingCost =
                calculateWelding(params);

        BigDecimal paintingCost =
                calculatePainting(params);

        BigDecimal turningCost =
                calculateTurning(params);

        BigDecimal totalWorkCost =
                laserCost
                        .add(bendingCost)
                        .add(weldingCost)
                        .add(paintingCost)
                        .add(turningCost);

        /*
         * Пока считаем прибыль как 0,
         * потому что в твоём Excel конкретная
         * логика прибыли ещё должна быть
         * точно перенесена.
         */
        BigDecimal profit =
                BigDecimal.ZERO;

        BigDecimal totalWithoutVat =
                materialCost
                        .add(totalWorkCost)
                        .add(profit);

        BigDecimal vat =
                totalWithoutVat
                        .multiply(VAT)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal totalWithVat =
                totalWithoutVat
                        .add(vat);

        return CalculationResult.builder()
                .materialCost(materialCost)
                .laserCost(laserCost)
                .bendingCost(bendingCost)
                .weldingCost(weldingCost)
                .paintingCost(paintingCost)
                .turningCost(turningCost)
                .totalWorkCost(totalWorkCost)
                .profit(profit)
                .totalWithoutVat(totalWithoutVat)
                .vat(vat)
                .totalWithVat(totalWithVat)
                .build();
    }

    private BigDecimal calculateMaterialCost(
            ExtractedParametersDto params,
            MaterialPrice material) {

        /*
         * Пока используем стоимость листа.
         *
         * ВАЖНО:
         * здесь потом надо точно воспроизвести
         * методику вашего Excel по количеству
         * листов/площади/раскрою.
         */

        if (material.getSheetPrice() == null) {
            return BigDecimal.ZERO;
        }

        return material.getSheetPrice()
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }

    private BigDecimal calculateLaser(
            ExtractedParametersDto params) {

        OperationsParameters operations =
                params.getOperations();

        if (operations == null ||
                operations.getLaser() == null) {

            return BigDecimal.ZERO;
        }

        LaserCuttingParameters laser =
                operations.getLaser();

        /*
         * Здесь намеренно НЕ пытаемся
         * угадать время из размеров детали.
         *
         * ИИ должен передать технологический
         * параметр, если он может его определить.
         */

        if (laser.getCuttingLength() == null) {
            return BigDecimal.ZERO;
        }

        /*
         * Временная формула.
         *
         * Реальную скорость резки нужно
         * взять из вашей технологической
         * методики/Excel.
         */
        BigDecimal hours =
                laser.getCuttingLength()
                        .divide(
                                new BigDecimal("1000"),
                                4,
                                RoundingMode.HALF_UP
                        );

        return hours
                .multiply(LASER_HOUR_PRICE)
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }

    private BigDecimal calculateBending(
            ExtractedParametersDto params) {

        if (params.getOperations() == null ||
                params.getOperations().getBending() == null) {

            return BigDecimal.ZERO;
        }

        Integer bends =
                params.getOperations()
                        .getBending()
                        .getBendCount();

        if (bends == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal hours =
                BigDecimal.valueOf(bends)
                        .divide(
                                BENDS_PER_HOUR,
                                4,
                                RoundingMode.HALF_UP
                        );

        return hours
                .multiply(BENDING_HOUR_PRICE)
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }

    private BigDecimal calculateWelding(
            ExtractedParametersDto params) {

        if (params.getOperations() == null ||
                params.getOperations().getWelding() == null) {

            return BigDecimal.ZERO;
        }

        WeldingParameters welding =
                params.getOperations()
                        .getWelding();

        if (welding.getWeldingLength() == null) {
            return BigDecimal.ZERO;
        }

        /*
         * Реальную производительность сварки
         * нужно брать из вашей методики.
         */
        BigDecimal hours =
                welding.getWeldingLength()
                        .divide(
                                new BigDecimal("1000"),
                                4,
                                RoundingMode.HALF_UP
                        );

        return hours
                .multiply(WELDING_HOUR_PRICE)
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }

    private BigDecimal calculatePainting(
            ExtractedParametersDto params) {

        if (params.getOperations() == null ||
                params.getOperations().getPainting() == null) {

            return BigDecimal.ZERO;
        }

        PaintingParameters painting =
                params.getOperations()
                        .getPainting();

        if (painting.getAreaM2() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal hours =
                painting.getAreaM2()
                        .divide(
                                PAINTING_M2_PER_HOUR,
                                4,
                                RoundingMode.HALF_UP
                        );

        return hours
                .multiply(PAINTING_HOUR_PRICE)
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }

    private BigDecimal calculateTurning(
            ExtractedParametersDto params) {

        if (params.getOperations() == null ||
                params.getOperations().getTurning() == null) {

            return BigDecimal.ZERO;
        }

        TurningParameters turning =
                params.getOperations()
                        .getTurning();

        if (turning.getMachineHours() == null) {
            return BigDecimal.ZERO;
        }

        return turning.getMachineHours()
                .multiply(TURNING_HOUR_PRICE)
                .multiply(
                        BigDecimal.valueOf(
                                params.getQuantity()
                        )
                );
    }
}