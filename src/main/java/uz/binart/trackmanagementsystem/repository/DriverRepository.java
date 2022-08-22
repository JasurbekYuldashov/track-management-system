package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.dto.DriverUnitDto;
import uz.binart.trackmanagementsystem.model.Driver;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {

    Driver findFirstByTruckId(Long truckId);

    List<Driver> findAllByTruckId(Long truckId);

    @Query("select d.id, d.firstName, d.lastName from Driver d where d.isActive = true and d.deleted = false")
    List<Object[]> findAllForContext();

}
