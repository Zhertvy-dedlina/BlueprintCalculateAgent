package ru.vsu.zhertvydedlina.aiservice.calculation.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OperationsParameters {

    private LaserCuttingParameters laser;
    private BendingParameters bending;
    private WeldingParameters welding;
    private PaintingParameters painting;
    private TurningParameters turning;
}