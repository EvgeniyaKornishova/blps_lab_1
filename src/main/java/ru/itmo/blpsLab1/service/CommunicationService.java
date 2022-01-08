package ru.itmo.blpsLab1.service;

import org.springframework.stereotype.Service;
import ru.itmo.blpsLab1.data.Notification;

public interface CommunicationService {
    void sendNotificationToEmail(Notification notification);
    void sendNotificationToBrowser(Notification notification);
    void sendNotificationToMobile(Notification notification);
}

@Service
class CommunicationServiceStub implements CommunicationService {
        @Override
        public void sendNotificationToEmail(Notification notification){};

        @Override
        public void sendNotificationToBrowser(Notification notification){};

        @Override
        public void sendNotificationToMobile(Notification notification){};
}
