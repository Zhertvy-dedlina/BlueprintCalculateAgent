package ru.vsu.zhertvydedlina.aiservice.calculation.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CalculationResult {

    private BigDecimal materialCost;

    private BigDecimal laserCost;

    private BigDecimal bendingCost;

    private BigDecimal weldingCost;

    private BigDecimal paintingCost;

    private BigDecimal turningCost;

    private BigDecimal totalWorkCost;

    private BigDecimal profit;

    private BigDecimal totalWithoutVat;

    private BigDecimal vat;

    private BigDecimal totalWithVat;
}