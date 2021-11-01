package ru.itmo.blps_lab_1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.itmo.blps_lab_1.data.Equity;
import ru.itmo.blps_lab_1.data.Notification;
import ru.itmo.blps_lab_1.data.NotificationRule;
import ru.itmo.blps_lab_1.repository.EquityRepository;
import ru.itmo.blps_lab_1.repository.NotificationRepository;
import ru.itmo.blps_lab_1.repository.NotificationRuleRepository;
import ru.itmo.blps_lab_1.repository.UserRepository;

import java.util.*;

@Service
public class DataUpdaterDaemon {
    @Autowired
    EquityRepository equityRepository;
    @Autowired
    NotificationRuleRepository notificationRuleRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    CommunicationService communicationService;

    @Scheduled(cron = "0 * * * * ?")
    private void processUpdate() {
        System.out.println("Demon start update");
        fetchEquitiesData();
        recalculateFinancialParameters();
        checkNotificationRules();
    }

    // TODO: replace with real fetch
    private void fetchEquitiesData() {
        Random rnd = new Random();
        rnd.setSeed(1024);

        List<Equity> equities = equityRepository.findAll();

        for (Equity equity : equities) {
            equity.setPrev(equity.getLast());
            equity.setLast(equity.getLast() * (1.5F - rnd.nextFloat() * 0.3F));
        }

        equityRepository.saveAll(equities);
    }

    // TODO: recalculate all parameters
    private void recalculateFinancialParameters() {
        List<Equity> equities = equityRepository.findAll();

        for (Equity equity : equities) {
            equity.setHigh(Math.max(equity.getLast(), equity.getHigh()));
            equity.setLow(Math.min(equity.getLast(), equity.getLow()));
        }

        equityRepository.saveAll(equities);
    }

    private void checkNotificationRules() {
        boolean notificationRuleTriggered;

        List<NotificationRule> notificationRules = notificationRuleRepository.findAll();
        Set<NotificationRule> notificationRulesForDelete = new HashSet<>();
        for (NotificationRule notificationRule : notificationRules) {
            notificationRuleTriggered = false;

            switch (notificationRule.getOp()) {
                case LESS:
                    notificationRuleTriggered = notificationRule.getEquity().getLast() < notificationRule.getValue();
                    break;
                case GREATER:
                    notificationRuleTriggered = notificationRule.getEquity().getLast() > notificationRule.getValue();
                    break;
            }

            if (notificationRuleTriggered) {
                sendNotification(notificationRule);

                if (notificationRule.getOnce())
                    notificationRulesForDelete.add(notificationRule);
            }
        }
        notificationRuleRepository.deleteAll(notificationRulesForDelete);
    }

    private void sendNotification(NotificationRule notificationRule) {
        Notification notification = new Notification();

        notification.setMessage(notificationRule.getEquity().getSymbol() + " " + notificationRule.getOp().toString().toLowerCase() + " than " + notificationRule.getValue());
        notification.setUser(notificationRule.getUser());

        notificationRepository.save(notification);

        communicationService.sendNotificationToEmail(notification);
        communicationService.sendNotificationToBrowser(notification);
        communicationService.sendNotificationToMobile(notification);
    }
}
