package ru.vsu.zhertvydedlina.aiservice.calculation.entity;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaintingParameters {

    private BigDecimal areaM2;

    private Integer quantity;

    private String coating;

    private String ral;
}