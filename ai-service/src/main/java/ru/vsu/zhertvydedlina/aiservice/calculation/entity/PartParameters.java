package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import java.math.BigDecimal;

public class PartParameters {

    private String material;
    private BigDecimal thickness;

    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    private Integer quantity;

    private BigDecimal area;
    private BigDecimal weight;

    private OperationsParameters operations;
}