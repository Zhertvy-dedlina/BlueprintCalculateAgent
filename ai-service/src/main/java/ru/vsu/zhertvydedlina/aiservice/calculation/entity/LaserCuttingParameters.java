package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LaserCuttingParameters {
    private BigDecimal cuttingLength;

    private Integer piercingCount;

    private BigDecimal machineHours;

    private BigDecimal setupHours;

    private Integer quantity;

    private String description;
}
