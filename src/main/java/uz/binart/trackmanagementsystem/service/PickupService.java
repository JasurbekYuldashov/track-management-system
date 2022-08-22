package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.Pickup;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PickupService {

    Pickup save(Pickup pickup, Long userId);

    void deleteById(Long id, Long userId);

    Page<Pickup> findFiltered(Pageable pageable);

    Pickup getById(Long id);

    void saveLoadIdToNextPickups(List<Long> pickupIds, Long loadId);

    List<Pickup> findAllForUpdateBefore(Date currentDate, Boolean active, Boolean completed);

    List<Pickup> findAllForUpdateAfter(Date currentDate, Boolean active, Boolean completed);

    void update(Pickup pickup, Long userId);

    Boolean existsById(Long id);

    Long minPickupTime(List<Long> pickupIds);

    Long maxPickupTime(List<Long> pickupIds);

    void addFileToDriversUploads(Long fileInfoId, Long pickupId);

    List<Map<String, Object>> getUploadsWithIdsAndNames(Long pickupId);

    void setDriversPickupTime(Long pickupId, Long driversPickupTime);

    List<Long> getSortedByTimeAsc(List<Long> pickups);

}
