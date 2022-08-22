package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.Delivery;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DeliveryService {

    Delivery save(Delivery delivery, Long userId);

    Delivery getById(Long id);

    void deleteById(Long id, Long userId);

    Page<Delivery> findFiltered(Pageable pageable);

    void setLoadIdToNextDeliveries(List<Long> deliveryIds, Long loadId, Long userId);

    List<Delivery> findAllForUpdate(Date currentDate, Boolean active, Boolean completed);

    List<Delivery> findAllForUpdateDelivered(Date currentDate, Boolean active, Boolean completed);

    void update(Delivery delivery, Long userId);

    void update_(Delivery delivery);

    Boolean existsById(Long id);

    Long minDeliveryTime(List<Long> deliveries);

    Long maxDeliveryTime(List<Long> deliveryIds);

    void addFileToDriversUploads(Long fileInfoId, Long pickupId);

    List<Map<String, Object>> getUploadsWithIdsAndNames(Long pickupId);

    void setDriversDeliveryTime(Long pickupId, Long driversPickupTime);

    List<Long> getSortedByTimeAsc(List<Long> ids);

}
