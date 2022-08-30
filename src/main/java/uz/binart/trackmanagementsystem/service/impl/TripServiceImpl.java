package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.TripDto;
import uz.binart.trackmanagementsystem.dto.TripForm;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.repository.TripRepository;
import uz.binart.trackmanagementsystem.repository.UnitRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UnitService unitService;
    private final UnitTypeService unitTypeService;
    private final SequenceService sequenceService;
    private final UnitRepository unitRepository;
    private final DeliveryService deliveryService;
    private final LoadService loadService;
    private final PickupService pickupService;
    private final UtilService utilService;
    private final DriverService driverService;
    private final ActionService actionService;

    public Trip save(Trip trip, Long userId){

        if(trip.getId() != null && tripRepository.existsById(trip.getId()))
            throw new IllegalChangeAttemptException();

        if(trip.getTruckId() != null && !unitService.existsById(trip.getTruckId()))
            throw new WrongEntityStructureException();

        if(trip.getTrailerId() != null && !unitService.existsById(trip.getTrailerId()))
            throw new WrongEntityStructureException();

        loadService.setDriverIdAndTruckIdToNextLoads(trip.getLoadIds(), trip.getDriverId(), trip.getTruckId());

        List<Long> loads = trip.getLoadIds();

        Load load = loadService.findById(loads.get(0));
        trip.setEmployerId(load.getOwnedCompanyId());

        Long pickupTime = load.getStartTime();

        Unit unit = unitService.getById(trip.getTruckId());
        unit.setUnitStatusId(resolveTimeForUnit(load.getStartTime(), load.getEndTime()));

        unit.setCurrentEmployerId(load.getOwnedCompanyId());

        trip.setTripStatusId(resolveTimeForTrip(load.getStartTime(), load.getEndTime()));

        unit.setCurrentEmployerId(trip.getEmployerId());
        Long currentDateTimestamp = new Date().getTime();
        Driver driver = driverService.findById(trip.getDriverId());

        if(pickupTime <= currentDateTimestamp){
            driver.setStatusId(3L);
            driverService.update(driver);
        }else {
            driver.setStatusId(4L);
            driverService.update(driver);
        }

        sequenceService.updateSequence("trips");

        trip.setActiveLoadId(trip.getLoadIds().get(0));
        trip.setOwnedCompanyId(loadService.findById(trip.getLoadIds().get(0)).getOwnedCompanyId());

        Trip savedTrip = tripRepository.save(trip);
        unit.setLastTripId(savedTrip.getId());
        unitService.update(unit);
        loadService.setTripId(savedTrip.getLoadIds(), savedTrip.getId(), userId);

        return savedTrip;
    }

    public Trip save(Trip trip){
        return tripRepository.save(trip);
    }

    public Optional<Trip> getById(Long id){
        return tripRepository.findById(id);
    }

    public void deleteById(Long id, Long userId){
        if(!tripRepository.existsById(id))
            throw new NotFoundException();

        Trip trip = tripRepository.getOne(id);

        if(!trip.getTripStatusId().equals(3L)) {
            Long loadId = trip.getActiveLoadId();
            loadService.setNotDispatched(loadId);
            Long unitId = trip.getTruckId();
            unitService.detachTripAndSetReady(unitId);
        }
        trip.setDeleted(true);
        tripRepository.save(trip);
    }

    public Page<Trip> findFiltered(Pageable pageable){
        return tripRepository.findAll(pageable);
    }

    public Page<Trip> findFiltered2(TripDto tripDto, Pageable pageable){
        return tripRepository.findAll(getFilteringSpecification(tripDto), pageable);
    }

    public Page<Trip> findFiltered(Long driverId, Pageable pageable){
        return tripRepository.findAll(getFilteringSpecification(driverId), pageable);
    }

    public void update(Trip trip){
        tripRepository.save(trip);
    }

    public void updateTrip(TripForm tripForm){

        Trip oldTrip = tripRepository.getOne(tripForm.getId());

        Trip newTrip = oldTrip;

        if(!newTrip.getTruckId().equals(tripForm.getTruckId())){
            unitService.detachTripAndSetReady(newTrip.getTruckId());
        }

        newTrip.setCustomTripNumber(tripForm.getCustomTripNumber());
        newTrip.setDriverId(tripForm.getDriverId());

        if(tripForm.getSecondDriverId() != null)
            newTrip.setSecondDriverId(tripForm.getSecondDriverId());
        newTrip.setTruckId(tripForm.getTruckId());
        newTrip.setDriverInstructions(tripForm.getDriverInstructions());
        List<Long> oldLoadIds = oldTrip.getLoadIds();
        List<Long> newLoadIds = tripForm.getLoadIds();

        loadService.updateLoads(oldLoadIds, newLoadIds);
        newTrip.setLoadIds(newLoadIds);
        loadService.setTripId(newLoadIds, newTrip.getId(), null);

        newTrip.setOdometer(tripForm.getOdometer());
        tripRepository.save(newTrip);
    }

    private Specification<Trip> getFilteringSpecification(TripDto tripDto){
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(tripDto.getVisibleIds() != null) {
                List<Predicate> visibilityPredicates = new ArrayList<>();
                for(Long id: tripDto.getVisibleIds()){
                    visibilityPredicates.add(criteriaBuilder.equal(root.get("employerId"), id));
                }
                predicates.add(criteriaBuilder.or(visibilityPredicates.toArray(new Predicate[0])));

            }

            if(tripDto.getOwnedCompanyId() != null)
                predicates.add(criteriaBuilder.equal(root.get("ownedCompanyId"), tripDto.getOwnedCompanyId()));

            if(tripDto.getStatusId() != null)
                predicates.add(criteriaBuilder.equal(root.get("tripStatusId"), tripDto.getStatusId()));

            if(tripDto.getId() != null && tripDto.getTruckNumber() != null)
                predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("id"), tripDto.getId()), root.get("truckId").in(unitRepository.getIdsByNumber("%" + tripDto.getTruckNumber().toLowerCase() + "%"))));
            else if(tripDto.getTruckNumber() != null && tripDto.getLoadNumber() != null) {
                Predicate truckNumberPredicate = root.get("truckId").in(unitRepository.getIdsByNumber("%" + tripDto.getTruckNumber().toLowerCase() + "%"));
                Predicate loadNumberPredicate = root.get("activeLoadId").in(loadService.findAllIdsByNumber("%" + tripDto.getLoadNumber().toLowerCase() + "%"));
                predicates.add(criteriaBuilder.or(truckNumberPredicate, loadNumberPredicate));
            }
            predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("deleted"), false), criteriaBuilder.isNull(root.get("deleted"))));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



    private Specification<Trip> getFilteringSpecification(Long driverId){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("driverId"), driverId);
            return criteriaBuilder.and(predicate);
        });
    }

    public Page<Trip> getByTruckIdLast(Long id){
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        return tripRepository.findByTruckIdAndDeletedIsFalse(id, pageable);
    }

    public Load getTripsLoad(Trip trip){
        List<Long> loadIds = trip.getLoadIds();
        if(!loadIds.isEmpty()){
            return loadService.findById(loadIds.get(0));
        }
        return null;
    }

    public ArrayList<Long> getTruckLoadsIds(Long tripId) {
        return null;
//        return tripRepository.getTruckLoadsIds(tripId);
    }

    private Long resolveTimeForUnit(Long timeStart, Long timeEnd){
        Long currentTime = utilService.getTimeStampWithOffset();

        if(currentTime < timeStart)
            return 2L;
        else if(currentTime >= timeStart && currentTime < timeEnd)
            return 1L;
        else return 3L;
    }

    private Long resolveTimeForTrip(Long timeStart, Long timeEnd){

        Long currentTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -11);
        currentTime = calendar.getTimeInMillis();

        if(currentTime < timeStart)
            return 1L;
        else if(currentTime < timeEnd)
            return 2L;
        else return 3L;

    }


}
