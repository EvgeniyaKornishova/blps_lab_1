package ru.itmo.blps_lab_1.data.dto;

import lombok.Data;
import ru.itmo.blps_lab_1.data.CompareOperator;

@Data
public class NotificationRuleInDto {
    private Boolean once;

    private Double value;

    private CompareOperator op;
}
