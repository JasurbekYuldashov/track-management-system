package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.CountingDto;
import uz.binart.trackmanagementsystem.dto.DeliveryDto;
import uz.binart.trackmanagementsystem.dto.PickupDto;
import uz.binart.trackmanagementsystem.exception.*;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.Trip;
import uz.binart.trackmanagementsystem.repository.*;
import uz.binart.trackmanagementsystem.service.*;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadServiceImpl implements LoadService {

    private final LoadRepository loadRepository;
    private final PickupService pickupService;
    private final OwnedCompanyRepository ownedCompanyRepository;
    private final DeliveryService deliveryService;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final TripRepository tripRepository;
    private final UnitRepository unitRepository;
    private final UtilService utilService;
    private final TimeService timeService;
    private final CompanyService companyService;

    public Load save(Load load, Long userId) {

        validateLoad(load, false);

        if (loadRepository.existsByCustomLoadNumberAndDeletedFalse(load.getCustomLoadNumber()))
            throw new WrongEntityStructureException("load with such number already exists");

        if (load.getDeliveries() != null && !load.getDeliveries().isEmpty())
            load.setActiveDeliveryId(load.getDeliveries().get(0));

        if (load.getPickups() != null && !load.getPickups().isEmpty())
            load.setActivePickupId(load.getPickups().get(0));

        List<Long> pickups = load.getPickups();
        List<Long> deliveries = load.getDeliveries();

        sequenceService.updateSequence("loads");
        Long pickupsMinTime = pickupService.minPickupTime(pickups);
        Long deliveriesMaxTime = deliveryService.maxDeliveryTime(deliveries);

        load.setStartTime(pickupsMinTime);
        load.setEndTime(deliveriesMaxTime);

        setSortedStopsIds(load);
        setCorrectedStartAndEndTime(load);

        Calendar calendar = Calendar.getInstance();

        load.setCreatedTime(calendar.getTimeInMillis());
        calendar.setTimeInMillis(deliveriesMaxTime);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        load.setPickupDeliveryCanBeUpdatedUntil(calendar.getTimeInMillis());

        Load savedLoad = loadRepository.save(load);

        deliveryService.setLoadIdToNextDeliveries(deliveries, savedLoad.getId(), userId);
        pickupService.saveLoadIdToNextPickups(pickups, savedLoad.getId());
        log.info("Load with " + savedLoad.getId() + " was created by user with " + userId + " id");
        actionService.captureCreate(savedLoad, "loads", userId);

        return savedLoad;
    }

    public Load save(Load load) {
        return loadRepository.save(load);
    }


    private void setCorrectedStartAndEndTime(Load load) {
        Pair<PickupDto, DeliveryDto> pair = utilService.getFirstAndLastStopsOnlyLocations2(load.getSortedPickupAndDeliveryIds().get(0), load.getSortedPickupAndDeliveryIds().get(load.getSortedPickupAndDeliveryIds().size() - 1));
        PickupDto pickupDto = pair.getFirst();
        DeliveryDto deliveryDto = pair.getSecond();
        Company shipper = companyService.getById(pickupDto.getShipperCompanyId());
        Company consignee = companyService.getById(deliveryDto.getConsigneeCompanyId());

        Pair<Long, Long> centralStartAndEndTime = timeService.getCorrectedByCentralTimeTimestamps(pickupDto.getPickupDate(), deliveryDto.getDeliveryDate(), shipper, consignee);

        load.setCentralStartTime(centralStartAndEndTime.getFirst());
        load.setCentralEndTime(centralStartAndEndTime.getSecond());
    }

    public Page<Load> findFiltered(Long tripId, String number, List<Long> visibleIds, Pageable pageable) {
        return loadRepository.findAll(getFilteringSpecification(tripId, number, visibleIds), pageable);
    }

    public List<Load> findAllForAccounting(Long carrierId, Long truckId, Long driverId, Long teamId, Long allByCompanysTruckId, Long startTime, Long endTime, Boolean weekly) {
        List<Long> properLoadIds = loadRepository.findProperLoadsIds(startTime, endTime);
        Set<Long> matchedCarrierLoadIds = tripRepository.findRequiredIdsByCompanyId2(properLoadIds, carrierId);
        Set<Long> matchedByTruckId = tripRepository.findRequiredIdsByTruck(truckId);
        Set<Long> matchedByDriverId = tripRepository.findRequiredIdsByDriverIds(driverId);
        Set<Long> teamUnitIds = unitRepository.findRequiredViaTeamId(teamId);
        Set<Long> teamIds = tripRepository.findRequiredIdsByTruckIdsIn(teamUnitIds);
        Set<Long> loadIdsByCompanySTruck = new HashSet<>();
        if (allByCompanysTruckId != null && ownedCompanyRepository.existsById(allByCompanysTruckId)) {
            String abbreviation = ownedCompanyRepository.findAbbreviationViaId(allByCompanysTruckId);
            Set<Long> truckIdsAllByCompany = unitRepository.getIdsByNumber("%" + abbreviation.toLowerCase());
            loadIdsByCompanySTruck = tripRepository.loadIdsByTruckIds2(properLoadIds, truckIdsAllByCompany);
        }
        List<Set<Long>> splitSets = split(teamIds, 10);
        List<Load> loads = new ArrayList<>();
//        log.warn(String.valueOf(splitSets.get(0)));
//        log.warn(String.valueOf(splitSets.get(1)));
//        log.warn(String.valueOf(splitSets.get(2)));
        for (int i = 0; i < 10; i++) {
            Set<Long> first = splitSets.get(i);
            if (first.size() > 0 | i == 0) {
                loads.addAll(loadRepository.findAll(
                        accountingFilteringSpecification(
                                matchedCarrierLoadIds,
                                matchedByTruckId,
                                matchedByDriverId,
                                splitSets.get(i),
                                loadIdsByCompanySTruck,
                                startTime,
                                endTime,
                                weekly),
                        Sort.by(Sort.Direction.ASC, "endTime")
                ));
            }
        }
        return loads;

        //     return loadRepository.findAll();
//        return loadRepository.findAll(
//                accountingFilteringSpecification(
//                        matchedCarrierLoadIds,
//                        matchedByTruckId,
//                        matchedByDriverId,
////                        teamIds,
//                        first,
//                        loadIdsByCompanySTruck,
//                        startTime,
//                        endTime,
//                        weekly),
//                Sort.by(Sort.Direction.ASC, "endTime")
//        );
    }

    public static <T> List<Set<T>> split(Set<T> original, int count) {
        // Create a list of sets to return.
        ArrayList<Set<T>> result = new ArrayList<Set<T>>(count);

        // Create an iterator for the original set.
        Iterator<T> it = original.iterator();

        // Calculate the required number of elements for each set.
        int each = original.size() / count;

        // Create each new set.
        for (int i = 0; i < count; i++) {
            HashSet<T> s = new HashSet<T>(original.size() / count + 1);
            result.add(s);
            for (int j = 0; j < each && it.hasNext(); j++) {
                s.add(it.next());
            }
        }
        return result;
    }

    private Specification<Load> accountingFilteringSpecification(Set<Long> matchedCarrierLoadIds, Set<Long> matchedByTruckId, Set<Long> matchedByDriverId, Set<Long> matchedByTeamId, Set<Long> allByCompanyIds, Long startTime, Long endTime, Boolean weekly) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

//            Set<Long> allMatchedIds = new HashSet<>();
//            allMatchedIds.addAll(matchedCarrierLoadIds);
//            allMatchedIds.addAll(matchedByTruckId);
//            allMatchedIds.addAll(matchedByDriverId);
//            allMatchedIds.addAll(matchedByTeamId);

          /*  if(!allMatchedIds.isEmpty()){
                List<Predicate> idPredicates = new ArrayList<>();
                for(Long id: allMatchedIds){
                    idPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(idPredicates.toArray(new Predicate[0])));
            }
*/
            if (!matchedCarrierLoadIds.isEmpty()) {
                List<Predicate> carrierPredicates = new ArrayList<>(matchedCarrierLoadIds.size());
                for (Long id : matchedCarrierLoadIds) {
                    carrierPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(carrierPredicates.toArray(new Predicate[0])));
            }
            if (!matchedByTruckId.isEmpty()) {
                List<Predicate> truckPredicates = new ArrayList<>(matchedByTruckId.size());
                for (Long id : matchedByTruckId) {
                    truckPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(truckPredicates.toArray(new Predicate[0])));
            }

            if (!matchedByDriverId.isEmpty()) {
                List<Predicate> driverPredicates = new ArrayList<>(matchedByDriverId.size());
                for (Long id : matchedByDriverId) {
                    driverPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(driverPredicates.toArray(new Predicate[0])));
            }

            if (!matchedByTeamId.isEmpty()) {
                List<Predicate> teamPredicates = new ArrayList<>(matchedByTeamId.size());
                for (Long id : matchedByTeamId) {
                    teamPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(teamPredicates.toArray(new Predicate[0])));
            }

            if (!allByCompanyIds.isEmpty()) {
                List<Predicate> allByCompanyPredicates = new ArrayList<>(allByCompanyIds.size());
                for (Long id : allByCompanyIds) {
                    allByCompanyPredicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.or(allByCompanyPredicates.toArray(new Predicate[0])));
            }

//            System.out.println("Times");
//            System.out.println(startTime);
//            System.out.println(endTime);

            Predicate startTimePredicate = criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("centralStartTime"), startTime), criteriaBuilder.lessThanOrEqualTo(root.get("centralStartTime"), endTime));
            Predicate endTimePredicate = criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("centralEndTime"), startTime), criteriaBuilder.lessThanOrEqualTo(root.get("centralEndTime"), endTime));

            if (weekly)
                predicates.add(criteriaBuilder.or(startTimePredicate, endTimePredicate));
            else
                predicates.add(endTimePredicate);

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("centralStartTime"), startTime));

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            predicates.add(criteriaBuilder.isNotNull(root.get("tripId")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        });
    }


/*    private Predicate glueWithPredicate(List<Long> ids, CriteriaBuilder criteriaBuilder, Root root){

        List<Predicate> carrierPredicates = new ArrayList<>(ids.size());
        for(Long id: ids){
            carrierPredicates.add(criteriaBuilder.equal(root.get("id"), id));
        }
        return criteriaBuilder.or(carrierPredicates.toArray(new Predicate[0]));

    }*/

    public List<Load> findUpcoming(Long time) {
        return loadRepository.findAllByStartTimeGreaterThanAndTripIdIsNotNullAndDeletedFalse(time);
    }

//    public List<Load> findUpcomingByTripId(Long tripId,Long time) {
//        return null;
//    }

    public List<Load> findUpcomingByTripId(Long tripId, Long time) {
        return loadRepository.findAllByTruckIdAndStartTimeIsGreaterThan(tripId,time);
    }

    public List<Load> findBetween(Long time) {
//        return loadRepository
        return loadRepository.findAllByStartTimeLessThanAndEndTimeGreaterThanAndTripIdIsNotNullAndDeletedFalse(time, time);
    }

    public List<Load> findBetween(Long time,Long time_) {
//        return loadRepository
        return loadRepository.findAllByStartTimeLessThanAndEndTimeGreaterThanAndTripIdIsNotNullAndDeletedFalse(time, time_);
    }

    public List<Load> findBetweenAndTruckId(Long time, Long last, Long truckId) {
//        return loadRepository.findAllByTruckIdBetweenAndCentralStartTimeIsAfterAndEndTimeLessThan(truckId,time,last);
        return loadRepository.findAll();
    }

    public List<Load> findAfter(Long time) {
        return loadRepository.findAllByEndTimeLessThanAndTripIdIsNotNullAndDeletedFalseAndUpdatedAsHistoryFalse(time);
    }

    public Load findById(@NotNull Long id) {
        Optional<Load> load = loadRepository.findById(id);
        if (!load.isPresent()) {
            log.warn("not found in " + this.getClass() + " service");
            throw new NotFoundException("no load with such id id database");
        } else return load.get();
    }

    public List<Long> findAllIdsByNumber(String loadNumber) {
        return loadRepository.findProperIds(loadNumber);
    }

    public void deleteById(Long id, Long userId) {
        if (!loadRepository.existsById(id))
            throw new NotFoundException("no load with such id for delete");
        Load deletingLoad = loadRepository.getOne(id);
        deletingLoad.setDeleted(true);
        actionService.captureDelete(deletingLoad, "loads", userId);
        loadRepository.save(deletingLoad);
    }

    public Boolean isAllFree(List<Long> loadIds) {
        for (Long id : loadIds) {
            Load load = loadRepository.getOne(id);
            if (load.getTripId() != null)
                return false;
        }
        return true;
    }

    public void update(Load load, Long userId) {

        validateLoad(load, true);

        if (loadRepository.existsByCustomLoadNumberAndIdNotAndDeletedFalse(load.getCustomLoadNumber(), load.getId()))
            throw new WrongEntityStructureException("another load's number violation");

        Load oldLoad = loadRepository.getOne(load.getId());
        if (oldLoad.getTripId() != null) {
            load.setTripId(oldLoad.getTripId());
            Trip trip = tripRepository.getOne(load.getTripId());
            trip.setEmployerId(load.getOwnedCompanyId());
            tripRepository.save(trip);
        }
        actionService.captureUpdate(oldLoad, load, "loads", userId);
        Long pickupsMinTime = pickupService.minPickupTime(load.getPickups());
        Long deliveriesMaxTime = deliveryService.maxDeliveryTime(load.getDeliveries());

        load.setStartTime(pickupsMinTime);
        load.setEndTime(deliveriesMaxTime);
        load.setCentralStartTime(pickupsMinTime);
        load.setCentralEndTime(deliveriesMaxTime);
        load.setUpdatedAsUpcoming(false);
        load.setUpdatedAsCovered(false);
        load.setUpdatedAsHistory(false);
        setSortedStopsIds(load);
//        setCorrectedStartAndEndTime(load);
        load.setBooked(oldLoad.getBooked());
        load.setDispute(oldLoad.getDispute());
        load.setDetention(oldLoad.getDetention());
        load.setAdditional(oldLoad.getAdditional());
        load.setFine(oldLoad.getFine());
        load.setRevisedInvoice(oldLoad.getRevisedInvoice());
        load.setFactoring(oldLoad.getFactoring());
        load.setTafs(oldLoad.getTafs());
        load.setNetPaid(oldLoad.getNetPaid());

        Load updatedLoad = loadRepository.save(load);

        if (updatedLoad.getTripId() != null) {
            Trip trip = tripRepository.getOne(updatedLoad.getTripId());
            if (trip != null) {
                trip.setTripStatusId(utilService.resolveTimeForTrip(updatedLoad.getStartTime(), updatedLoad.getEndTime()));
                tripRepository.save(trip);
            }
        }
        setSortedStopsIds(load);

        updateDeliveryTime(updatedLoad.getId());

    }

    public void updateLoads(List<Long> oldLoadIds, List<Long> newLoadIds) {
        for (Long oldId : oldLoadIds) {
            if (!newLoadIds.contains(oldId)) {
                Load load = loadRepository.getOne(oldId);
                load.setTripId(null);
                loadRepository.save(load);
            }
        }
    }

    public List<Load> findAllByTripId(Long tripId) {
        return loadRepository.findAllByTripId(tripId);
    }

    public Pair<List<Long>, List<Long>> getAllPickupsByTripId(Long tripId) {
        List<Load> loads = findAllByTripId(tripId);

        List<Long> pickups = new ArrayList<>();
        List<Long> deliveries = new ArrayList<>();

        for (Load load : loads) {
            pickups.addAll(load.getPickups());
            deliveries.addAll(load.getDeliveries());
        }

        return Pair.of(pickups, deliveries);
    }

    public void setSortedStopsIds(Load load) {

        List<Long> pickups = load.getPickups();
        List<Long> deliveries = load.getDeliveries();

        List<Object> sortedPickupsAndDeliveries = utilService.sortByPickupOrDeliveryDate(pickups, deliveries, false);

        List<Long> sortedIds = new ArrayList<>();
        for (Object object : sortedPickupsAndDeliveries) {
            try {
                PickupDto pickupDto = (PickupDto) object;
                sortedIds.add(pickupDto.getId());
            } catch (Exception exception) {
                assert false;
                DeliveryDto deliveryDto = (DeliveryDto) object;
                sortedIds.add(deliveryDto.getId());
            }
        }

        load.setSortedPickupAndDeliveryIds(sortedIds);

    }

    public Boolean checkIfTimeIsAcceptableForPickup(Long loadId, Long newTime) {
        Load load = findById(loadId);
        return load.getEndTime() != null && load.getEndTime() < newTime;
    }

    public void updatePickupTime(Long loadId) {

        Load load = findById(loadId);

        Long newMinTime = pickupService.minPickupTime(load.getPickups());

        load.setStartTime(newMinTime);
        loadRepository.save(load);
    }

    public Boolean checkIfTimeIsAcceptableForDelivery(Long loadId, Long newTime) {
        Load load = findById(loadId);
        return load.getStartTime() != null && load.getStartTime() < newTime;
    }

    public void updateDeliveryTime(Long loadId) {
        Load load = findById(loadId);

        List<Long> pickups = load.getPickups();

        Long pickupMaxTime = pickupService.maxPickupTime(pickups);
        Long deliveryMaxTime = deliveryService.maxDeliveryTime(load.getDeliveries());

        if (pickupMaxTime > deliveryMaxTime) {
            log.warn("couldn't update time for load with " + loadId + " id because of wrong pickup and delivery periods");
            throw new NotAcceptableException();
        }

        Long newStartTime = pickupService.minPickupTime(pickups);

        load.setStartTime(newStartTime);
        load.setEndTime(deliveryMaxTime);

        loadRepository.save(load);
    }

    public void setTripId(List<Long> loads, Long tripId, Long userId) {

        for (Long id : loads) {
            Load load = loadRepository.getOne(id);
            Load initial = load;
            load.setTripId(tripId);
            //   actionService.captureUpdate(initial, load, "loads", userId);
            update(load, userId);
        }
        log.info("trip id " + tripId + "  was set to loads with " + loads + " ids");
    }

    public void setDriverIdAndTruckIdToNextLoads(List<Long> loadIds, Long driverId, Long truckId) {
        for (Long id : loadIds) {
            Load load = loadRepository.getOne(id);
            load.setDriverId(driverId);
            load.setTruckId(truckId);
            loadRepository.save(load);
        }
    }

    private Specification<Load> getFilteringSpecification(Long tripId, String number, List<Long> visibleIds) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (visibleIds != null) {
                List<Predicate> visibilityPredicates = new ArrayList<>();
                for (Long id : visibleIds) {
                    visibilityPredicates.add(criteriaBuilder.equal(root.get("ownedCompanyId"), id));
                }
                predicates.add(criteriaBuilder.or(visibilityPredicates.toArray(new Predicate[0])));

            }


            if (number != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("customLoadNumber")), "%" + number.toUpperCase() + "%"));

            if (tripId == null)
                predicates.add(criteriaBuilder.isNull(root.get("tripId")));
            else if (tripId != -1)
                predicates.add(criteriaBuilder.equal(root.get("tripId"), tripId));

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private void validateLoad(Load load, Boolean update) {

        if (!update)
            if (load.getId() != null && loadRepository.existsById(load.getId())) {
                log.warn("Attempt to resave Load with " + load.getId() + " id");
                throw new UnitWithSuchNumberExistsAlreadyException("Attempt to resave Load with " + load.getId() + " id");
            }

        List<Long> pickups = load.getPickups();
        List<Long> deliveries = load.getDeliveries();

        List<Long> sortedPickups = pickupService.getSortedByTimeAsc(pickups);
        List<Long> sortedDeliveries = deliveryService.getSortedByTimeAsc(deliveries);

        load.setPickups(sortedPickups);
        load.setDeliveries(sortedDeliveries);

        Long minPickupTime = pickupService.minPickupTime(pickups);
        Long maxPickupTime = pickupService.maxPickupTime(pickups);

        Long minDeliveryTime = deliveryService.minDeliveryTime(deliveries);
        Long maxDeliveryTime = deliveryService.maxDeliveryTime(deliveries);

        if (minPickupTime > minDeliveryTime) {
            throw new WrongEntityStructureException("first delivery is before first pickup");
        }

        if (maxPickupTime > maxDeliveryTime) {
            throw new WrongEntityStructureException("last pickup is after last delivery");
        }

        if (load.getPickups() == null) {
            throw new WrongEntityStructureException("pickups should be specified");
        }

        if (load.getPickups().isEmpty()) {
            log.warn("Loads without pickups are not allowed");
            throw new WrongEntityStructureException("Pickups should be specified");
        }

        if (load.getDeliveries() == null) {
            throw new WrongEntityStructureException("deliveries should be specified");
        }

        if (load.getDeliveries().isEmpty()) {
            log.warn("Load without deliveries are not allowed");
            throw new WrongEntityStructureException("Deliveries should be specified");
        }

        if (!load.getPickups().isEmpty() && load.getPickups().contains(null)) {
            log.warn("attempt to add Load with invalid pickup id");
            throw new WrongEntityStructureException("invalid pickup id - null");
        }

        if (!load.getDeliveries().isEmpty() && load.getDeliveries().contains(null)) {
            log.warn("attempt to add load with invalid delivery id");
            throw new WrongEntityStructureException("invalid delivery id - null");
        }

        if (load.getCustomLoadNumber() == null || load.getCustomLoadNumber().isEmpty()) {
            throw new WrongEntityStructureException("load number should be specified");
        }

        if (load.getCustomerId() == null) {
            throw new WrongEntityStructureException("customer is should be specified");
        }

        if (load.getRcPrice() == null) {
            throw new WrongEntityStructureException("price should be specified");
        }

        if (load.getRcPrice() <= 0f) {
            throw new WrongEntityStructureException("price should be positive number");
        }

    }


    public void setCountingInformation(CountingDto counting) {
        if (counting.getLoadId() == null)
            throw new WrongEntityStructureException("id of counting load should be specified");

        Optional<Load> loadOptional = loadRepository.findById(counting.getLoadId());

        if (loadOptional.isEmpty())
            throw new NotFoundException("no load with such id");

        Load load = loadOptional.get();

        load.setBooked(counting.getBooked());
        load.setDispute(counting.getDispute());
        load.setDetention(counting.getDetention());
        load.setAdditional(counting.getAdditional());
        load.setFine(counting.getFine());
        float F = counting.getBooked() + counting.getDispute() + counting.getDetention() + counting.getAdditional() - counting.getFine();
        load.setRevisedInvoice(F);

        load.setFactoring(counting.getFactoring());
        load.setTafs(counting.getTafs());
        float I = counting.getFactoring() - counting.getTafs();
        load.setNetPaid(I);
        loadRepository.save(load);

    }

    public void setNotDispatched(Long loadId) {
        if (loadRepository.existsById(loadId)) {
            Load load = loadRepository.getOne(loadId);
            load.setStartTime(null);
            load.setEndTime(null);
            load.setTripId(null);
            loadRepository.save(load);
        }
    }

    public CountingDto getCountingInformation(Long loadId) {

        Optional<Load> loadOptional = loadRepository.findById(loadId);

        if (loadOptional.isEmpty())
            throw new NotFoundException("no load with such id");

        Load load = loadOptional.get();
        CountingDto dto = new CountingDto();
        if (load.getBooked() != null)
            dto.setBooked(load.getBooked());
        else dto.setBooked(0F);

        if (load.getDispute() != null)
            dto.setDispute(load.getDispute());
        else dto.setDispute(0F);

        if (load.getDetention() != null)
            dto.setDetention(load.getDetention());
        else dto.setDetention(0f);

        if (load.getAdditional() != null)
            dto.setAdditional(load.getAdditional());
        else dto.setAdditional(0f);

        if (load.getFine() != null)
            dto.setFine(load.getFine());
        else dto.setFine(0F);

        if (load.getRevisedInvoice() != null)
            dto.setRevisedInvoice(load.getRevisedInvoice());
        else dto.setRevisedInvoice(0F);

        if (load.getFactoring() != null)
            dto.setFactoring(load.getFactoring());
        else dto.setFactoring(0F);

        if (load.getTafs() != null)
            dto.setTafs(load.getTafs());
        else dto.setTafs(0F);

        if (load.getNetPaid() != null)
            dto.setNetPaid(load.getNetPaid());
        else dto.setNetPaid(0F);

        return dto;
    }

}
