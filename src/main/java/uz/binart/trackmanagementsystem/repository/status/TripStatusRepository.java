package uz.binart.trackmanagementsystem.repository.status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.status.TripStatus;

@Repository
public interface TripStatusRepository extends JpaRepository<TripStatus, Long> {
}
