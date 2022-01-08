package ru.itmo.blpsLab1.data.dto;

import lombok.Data;
import ru.itmo.blpsLab1.data.CompareOperator;
import ru.itmo.blpsLab1.data.NotificationRule;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NotificationRuleDto {
    private Long id;

    private Boolean once;

    private Double value;

    private CompareOperator op;

    private Long equityId;

    public static NotificationRuleDto fromNotificationRule(NotificationRule notificationRule){
        NotificationRuleDto notificationRuleDto = new NotificationRuleDto();

        notificationRuleDto.setId(notificationRule.getId());
        notificationRuleDto.setOnce(notificationRule.getOnce());
        notificationRuleDto.setValue(notificationRule.getValue());
        notificationRuleDto.setOp(notificationRule.getOp());
        notificationRuleDto.setEquityId(notificationRule.getEquity().getId());

        return notificationRuleDto;
    }

    public static List<NotificationRuleDto> fromNotificationRulesCollection(Collection<NotificationRule> notificationRules){
        return notificationRules.stream().map(NotificationRuleDto::fromNotificationRule).collect(Collectors.toList());
    }
}
