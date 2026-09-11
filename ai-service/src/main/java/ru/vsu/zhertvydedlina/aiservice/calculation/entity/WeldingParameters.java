package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeldingParameters {

    private BigDecimal weldingLength;

    private BigDecimal machineHours;

    private BigDecimal setupHours;

    private Integer quantity;

    private String description;
}
