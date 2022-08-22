package uz.binart.trackmanagementsystem.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.ExpirationNotification;

import java.util.List;

public interface ExpirationNotificationService {

    ExpirationNotification save(ExpirationNotification expirationNotification);

    Long totalNotSeen();

    List<ExpirationNotification> getActualNotifications();

    List<ExpirationNotification> getAsList(Pageable pageable);

    ExpirationNotification getById(Long id);

    Page<ExpirationNotification> getNotifications(Boolean onlyNotSeen, Pageable pageable);

    ExpirationNotification getByIdAndMarkAsSeen(Long id);

}
