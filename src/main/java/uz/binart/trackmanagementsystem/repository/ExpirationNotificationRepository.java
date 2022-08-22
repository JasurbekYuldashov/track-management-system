package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.ExpirationNotification;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ExpirationNotificationRepository extends JpaRepository<ExpirationNotification, Long>, JpaSpecificationExecutor<ExpirationNotification> {

    List<ExpirationNotification> findAllByWasSeenFalse(Sort sort);

    Page<ExpirationNotification> findByWasSeen(Boolean wasSeen, Pageable pageable);

    long countAllByWasSeenFalse();
}
