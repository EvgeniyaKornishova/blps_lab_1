package ru.itmo.blps_lab_1.data.dto;

import lombok.Data;
import ru.itmo.blps_lab_1.data.Notification;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NotificationDto {
    private Long id;
    private String message;
    private LocalDateTime time;

    public static NotificationDto fromNotification(Notification notification){
        NotificationDto notificationDto = new NotificationDto();

        notificationDto.setId(notification.getId());
        notificationDto.setMessage(notification.getMessage());
        notificationDto.setTime(notification.getTime());

        return notificationDto;
    }

    public static List<NotificationDto> fromNotificationsCollection(Collection<Notification> notifications){
        return notifications.stream().map(NotificationDto::fromNotification).collect(Collectors.toList());
    }
}
