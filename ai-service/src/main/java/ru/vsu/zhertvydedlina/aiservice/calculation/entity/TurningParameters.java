package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TurningParameters {

    private Integer quantity;

    private BigDecimal machineHours;

    private String description;
}