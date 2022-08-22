package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Load;

import java.util.List;

@Repository
public interface LoadRepository extends JpaRepository<Load, Long>, JpaSpecificationExecutor<Load> {

    List<Load> findAllByTripId(Long tripId);

    Boolean existsByCustomLoadNumberAndDeletedFalse(String customLoadNumber);

    Boolean existsByCustomLoadNumberAndIdNotAndDeletedFalse(String customLoadNumber, Long id);

    List<Load> findAllByDeletedFalseOrDeletedIsNull();

    List<Load> findAllByStartTimeGreaterThanAndTripIdIsNotNullAndDeletedFalse(Long time);

    List<Load> findAllByStartTimeLessThanAndEndTimeGreaterThanAndTripIdIsNotNullAndDeletedFalse(Long time, Long time_);

    List<Load> findAllByEndTimeLessThanAndTripIdIsNotNullAndDeletedFalseAndUpdatedAsHistoryFalse(Long time);

    List<Load> findAllByEndTimeLessThanAndTripIdIsNotNullAndDeletedFalse(Long time);

    @Query("select l.id from Load l where l.centralStartTime >= :startTime and l.centralStartTime <= :endTime or l.centralEndTime >= :startTime and l.centralEndTime <= :endTime")
    List<Long> findProperLoadsIds(Long startTime, Long endTime);

    @Query("select l.id from Load l where lower(l.customLoadNumber) like :loadNumber")
    List<Long> findProperIds(String loadNumber);

}
