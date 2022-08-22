package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.DriverSupportRequest;

@Repository
public interface DriverSupportRequestRepository extends JpaRepository<DriverSupportRequest, Long> {


}
