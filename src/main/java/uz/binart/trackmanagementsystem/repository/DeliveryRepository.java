package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Delivery;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {

    List<Delivery> findAllByDeliveryDateAfterAndActiveAndCompletedAndLoadIdNotNull(Date currentTime, Boolean active, Boolean completed);

    List<Delivery> findAllByDeliveryDateBeforeAndActiveAndCompletedAndLoadIdNotNull(Date currentTime, Boolean active, Boolean completed);

    List<Delivery> findAllByIdIn(List<Long> ids);

    Page<Delivery> findAllByDeletedFalse(Pageable pageable);

    @Query("select delivery.id from Delivery delivery where delivery.id in :ids order by delivery.deliveryDate asc")
    List<Long> getSortedByTime(List<Long> ids);

}
