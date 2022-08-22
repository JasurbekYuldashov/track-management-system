package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Pickup;

import java.util.Date;
import java.util.List;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long>, JpaSpecificationExecutor<Pickup> {

    List<Pickup> findAllByPickupDateBeforeAndActiveAndCompletedAndLoadIdNotNull(Date currentTime, Boolean active, Boolean completed);

    List<Pickup> findAllByPickupDateAfterAndActiveAndCompletedAndLoadIdNotNull(Date currentTime, Boolean active, Boolean completed);

    List<Pickup> findAllByIdIn(List<Long> ids);

    @Query("select pickup.id from Pickup pickup where pickup.id in :ids order by pickup.pickupDate asc")
    List<Long> getSortedByTime(List<Long> ids);

}
