package ru.itmo.blpsLab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.blpsLab1.data.NotificationRule;

@Repository
public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long> {
}
