package ru.itmo.blpsLab1.data.dto;

import lombok.Data;
import ru.itmo.blpsLab1.data.CompareOperator;

@Data
public class NotificationRuleInDto {
    private Boolean once;

    private Double value;

    private CompareOperator op;
}
