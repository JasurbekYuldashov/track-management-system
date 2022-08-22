package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Trip;

import java.util.List;
import java.util.Set;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {

    Page<Trip> findByTruckIdAndDeletedIsFalse(Long truckId, Pageable pageable);

    @Query("select t.activeLoadId from Trip t where t.employerId = :companyId")
    Set<Long> findRequiredIdsByCompanyId1(Long companyId);

    @Query("select t.activeLoadId from Trip t where t.activeLoadId in :loadIds and t.employerId = :companyId")
    Set<Long> findRequiredIdsByCompanyId2(List<Long> loadIds, Long companyId);

    @Query("select t.activeLoadId from Trip t where t.truckId = :truckId")
    Set<Long> findRequiredIdsByTruck(Long truckId);

    @Query("select t.activeLoadId from Trip t where t.driverId = :driverId")
    Set<Long> findRequiredIdsByDriverIds(Long driverId);

    @Query("select t.activeLoadId from Trip t where t.truckId in :ids")
    Set<Long> findRequiredIdsByTruckIdsIn(Set<Long> ids);

    @Query("select t.activeLoadId from Trip t where t.truckId in :ids")
    Set<Long> findRequiredByCompanySTruck(Set<Long> ids);

    @Query("select t.activeLoadId from Trip t where t.truckId in :ids")
    Set<Long> loadIdsByTruckIds(Set<Long> ids);

    @Query("select t.activeLoadId from Trip t where t.activeLoadId in :loadIds and t.truckId in :ids")
    Set<Long> loadIdsByTruckIds2(List<Long>loadIds, Set<Long> ids);

}
