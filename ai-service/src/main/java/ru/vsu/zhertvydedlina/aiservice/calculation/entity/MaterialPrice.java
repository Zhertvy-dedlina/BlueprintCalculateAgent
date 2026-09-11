package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialPrice {

    /**
     * Название материала.
     * Например: "3,0 мм Ст3 (3000х1500)"
     */
    private String name;

    /**
     * Цена за тонну.
     */
    private BigDecimal pricePerTon;

    /**
     * Площадь одного листа, м².
     */
    private BigDecimal sheetAreaM2;

    /**
     * Вес одного листа, тонн.
     */
    private BigDecimal sheetWeightTon;

    /**
     * Доплата за лазерную резку одного листа.
     */
    private BigDecimal sheetCuttingPrice;

    /**
     * Итоговая стоимость одного листа.
     */
    private BigDecimal sheetPrice;

    /**
     * Стоимость 1 м².
     */
    private BigDecimal pricePerM2;
}