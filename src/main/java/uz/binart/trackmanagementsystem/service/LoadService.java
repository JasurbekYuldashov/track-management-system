package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import uz.binart.trackmanagementsystem.dto.CountingDto;
import uz.binart.trackmanagementsystem.model.Load;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface LoadService {

    Load save(Load load, Long userId);

    Load save(Load load);

    Page<Load> findFiltered(Long tripId, String number, List<Long> visibleIds, Pageable pageable);

    List<Load> findAllForAccounting(Long carrierId, Long truckId, Long driverId, Long teamId, Long allByCompanysTruck, Long startTime, Long endTime, Boolean weekly);

    List<Load> findUpcoming(Long time);
    List<Load> findUpcomingByTripId(Long tripId,Long time);

    List<Load> findBetween(Long time);
    List<Load> findBetween(Long time,Long time_);
    List<Load> findBetweenAndTruckId(Long time,Long lastSaturday,Long truckId);

    List<Load> findAfter(Long time);

    Load findById(@NotNull Long id);

    List<Long> findAllIdsByNumber(String loadNumber);

    void deleteById(Long id, Long userId);

    Boolean isAllFree(List<Long> loadIds);

    void setTripId(List<Long> loads, Long tripId, Long userId);

    void setDriverIdAndTruckIdToNextLoads(List<Long> loadIds, Long driverId, Long truckId);

    void update(Load load, Long userId);

    void updateLoads(List<Long> oldLoadIds, List<Long> newLoadIds);

    List<Load> findAllByTripId(Long tripId);

    Pair<List<Long>, List<Long>> getAllPickupsByTripId(Long tripId);

    void setSortedStopsIds(Load load);

    Boolean checkIfTimeIsAcceptableForPickup(Long loadId, Long newTime);

    void updatePickupTime(Long loadId);

    Boolean checkIfTimeIsAcceptableForDelivery(Long loadId, Long newTime);

    void updateDeliveryTime(Long loadId);

    void setCountingInformation(CountingDto counting);

    CountingDto getCountingInformation(Long loadId);

    void setNotDispatched(Long loadId);


}
