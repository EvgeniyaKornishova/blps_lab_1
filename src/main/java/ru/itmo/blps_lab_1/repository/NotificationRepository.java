package ru.itmo.blps_lab_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.blps_lab_1.data.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
