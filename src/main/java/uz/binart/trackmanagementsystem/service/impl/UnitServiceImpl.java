package uz.binart.trackmanagementsystem.service.impl;

import com.google.api.client.util.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.UnitDto;
import uz.binart.trackmanagementsystem.exception.*;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.Unit;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.model.type.UnitType;
import uz.binart.trackmanagementsystem.repository.LoadRepository;
import uz.binart.trackmanagementsystem.repository.UnitRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;
import uz.binart.trackmanagementsystem.service.type.DriverTypeService;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitTypeService unitTypeService;
    private final UnitStatusService unitStatusService;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final DriverTypeService driverTypeService;
    private final TruckNoteService truckNoteService;
    private final LoadRepository loadRepository;

    public Unit create(Unit unit, Long userId){
        if(unitRepository.existsByNumber(unit.getNumber())){
            log.warn("unit with " + unit.getNumber() + " already exists");
            throw new UnitWithSuchNumberExistsAlreadyException();
        }
        if(!unitTypeService.existsById(unit.getUnitTypeId())){
            log.warn("unit with " + unit.getUnitTypeId() + " id");
            throw new NoSuchUnitTypeException();
        }
        if(!driverTypeService.existsById(unit.getOwnershipTypeId())){
            log.warn("no such driver type " + unit.getOwnershipTypeId());
            throw new NoSuchOwnershipTypeException();
        }
        if(unit.getId() != null){
            log.warn("unit id is not null");
            throw new IllegalChangeAttemptException();
        }
        sequenceService.updateSequence("units");



        unit.setIsActive(true);
        unit.setDeleted(false);
        Unit savedUnit = unitRepository.save(unit);

        String notes = savedUnit.getNotes();
        notes = StringUtils.trimToEmpty(notes);

        if(!notes.isEmpty()){
            truckNoteService.save(notes, savedUnit.getId(), userId);
        }

        actionService.captureCreate(savedUnit, "units", userId);

        return savedUnit;
    }

    public List<Unit> findByLicensePlateExpirationAfter(Long time){
        return unitRepository.findAllByLicensePlateExpirationTimeGreaterThanAndNotifiedOfLicensePlateExpiration(time, false);
    }

    public List<Unit> findByAnnualInspectionExpirationTimeAfter(Long time) {
        return unitRepository.findAllByAnnualInspectionExpirationTimeGreaterThanAndNotifiedOfInspection(time, false);
    }

    public List<Unit> findByRegistrationExpirationTimeAfter(Long time){
        return unitRepository.findAllByRegistrationExpirationTimeGreaterThanAndNotifiedOfRegistration(time, false);
    }

    public List<Unit> findAllDeletedFalse(){
        return unitRepository.findAllByDeletedFalse();
    }

    public String getUnitTypeName(Long unitId){
        Unit unit = getById(unitId);
        UnitType unitType = unitTypeService.getById(unit.getUnitTypeId());
        return unitType.getName();
    }

    public Unit update(Unit unit){
        return unitRepository.save(unit);
    }

    public Unit changeStatus(Long unitId, Long statusId, Long userId, Long eldUntil){

        if(!unitRepository.existsById(unitId))
            throw new NotFoundException("No such unit for status update");

        if(!unitStatusService.existsById(statusId))
            throw new WrongEntityStructureException();

        Unit currentUnit = unitRepository.getOne(unitId);

//        if(currentUnit.getUnitStatusId() != null && currentUnit.getUnitStatusId().equals(statusId))
//            return currentUnit;

        Unit unitWithNewStatus;
        unitWithNewStatus = currentUnit;

        if(statusId.equals(3L)){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -11);
            Long currentTime = calendar.getTimeInMillis();
            unitWithNewStatus.setReadyFrom(currentTime);
        }

        if(statusId.equals(7L)){
            unitWithNewStatus.setEldUnTil(eldUntil);
        }

        unitWithNewStatus.setUnitStatusId(statusId);

        unitRepository.save(unitWithNewStatus);
        actionService.captureUpdate(currentUnit, unitWithNewStatus, "units", userId);

        return unitWithNewStatus;
    }

    public Boolean existsByNumber(String number){
        return unitRepository.existsByNumber(number);
    }

    public Unit getByNumber(String number){
        return unitRepository.findByNumber(number);
    }

    public void deleteByNumber(String number, Long userId){
        if(!unitRepository.existsByNumber(number))
            throw new NotFoundException();
        Unit deletingUnit = unitRepository.findByNumber(number);
        actionService.captureDelete(deletingUnit, "units", userId);
        deletingUnit.setDeleted(true);
        unitRepository.save(deletingUnit);
    }

    public Page<Unit> findFiltered(UnitDto unitDto, Pageable pageable){
        return unitRepository.findAll(getFilteringSpecification(unitDto), pageable);
    }

    public List<Unit> findFiltered(UnitDto unitDto, Sort sort){
        return unitRepository.findAll(getFilteringSpecification(unitDto));
    }

    public List<Unit> findAllByType(Long unitTypeId){
        return unitRepository.findAllByUnitTypeId(unitTypeId);
    }

    public Boolean existsById(Long id){
        return unitRepository.existsById(id);
    }

    public Unit getById(@NotNull Long id){
        if(!unitRepository.existsById(id))
            log.warn("unable to fine unit with " + id + " id");
        return unitRepository.getOne(id);
    }

    public List<Unit> findByNumber(String number, List<Long> visibleIds){
        return unitRepository.findAll(numberSpecification(number, visibleIds));
    }

    private Specification<Unit> numberSpecification(String number, List<Long> visibleIds){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            String number_ = StringUtils.trimToEmpty(number);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("number")), "%" + number_.toUpperCase() + "%"));

            if(visibleIds != null){
                List<Predicate> employerPredicates = new ArrayList<>(visibleIds.size());

                for (Long id : visibleIds) {
                    employerPredicates.add(
                            criteriaBuilder.or(
                                    criteriaBuilder.equal(root.get("currentEmployerId"), id),
                                    criteriaBuilder.equal(root.get("employerId"), id)
                            ));
                }
                predicates.add(criteriaBuilder.or(employerPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }


    public Optional<Unit> getIfExists(Long id){
        return unitRepository.findById(id);
    }

    public void deleteById(Long id){
        unitRepository.deleteById(id);
    }

    public Optional<Unit> findById(Long id){
        return unitRepository.findById(id);
    }

    private Specification<Unit> getFilteringSpecification(UnitDto unitDto){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(6);

            if(unitDto.getNumber() != null)
                predicates.add(criteriaBuilder.like(root.get("number"),"%" + unitDto.getNumber() + "%"));

            if(unitDto.getVin() != null)
                predicates.add(criteriaBuilder.like(root.get("vin"), "%" + unitDto.getVin() + "%"));

            if(unitDto.getCurrentEmployerId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("employerId"), unitDto.getEmployerId())
                );
            }else{
                if(unitDto.getVisibleIds() != null) {
                    List<Long> visibleIds = unitDto.getVisibleIds();
                    List<Predicate> employerPredicates = new ArrayList<>(visibleIds.size());

                    for (Long id : visibleIds) {
                        employerPredicates.add(
                                criteriaBuilder.or(
                                        criteriaBuilder.equal(root.get("currentEmployerId"), id),
                                        criteriaBuilder.equal(root.get("employerId"), id)
                                ));
                    }
                    predicates.add(criteriaBuilder.or(employerPredicates.toArray(new Predicate[0])));
                }
            }

            if(unitDto.getUnitTypeId() != null)
                predicates.add(criteriaBuilder.equal(root.get("unitTypeId"), unitDto.getUnitTypeId()));

            if(unitDto.getUnitStatusId() != null)
                predicates.add(criteriaBuilder.equal(root.get("unitStatusId"), unitDto.getUnitStatusId()));

            if(unitDto.getOwnershipTypeId() != null)
                predicates.add(criteriaBuilder.equal(root.get("ownershipTypeId"), unitDto.getOwnershipTypeId()));

            if(unitDto.getStatus() != null)
                predicates.add(criteriaBuilder.equal(root.get("status"), unitDto.getStatus()));

            if(unitDto.getIsActive() != null)
                predicates.add(criteriaBuilder.equal(root.get("isActive"), unitDto.getIsActive()));

            if(unitDto.getTeamId() != null)
                predicates.add(criteriaBuilder.equal(root.get("teamId"), unitDto.getTeamId()));
            else{
                if(unitDto.getVisibleTeamIds() != null){
                    List<Long> visibleTeamIds = unitDto.getVisibleTeamIds();
                    List<Predicate> visibleTeamsPredicates = new ArrayList<>();

                    for(Long id: visibleTeamIds){
                        visibleTeamsPredicates.add(criteriaBuilder.equal(root.get("teamId"), id));
                    }
                    visibleTeamsPredicates.add(criteriaBuilder.isNull(root.get("teamId")));
                    predicates.add(criteriaBuilder.or(visibleTeamsPredicates.toArray(new Predicate[0])));
                }
            }



            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Specification<Unit> getVisibilityFilteringSpecification(List<Long> visibleIds, List<Long> visibleTeamIds){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(visibleIds != null) {

                List<Predicate> employerPredicates = new ArrayList<>(visibleIds.size());

                for (Long id : visibleIds) {
                    employerPredicates.add(
                            criteriaBuilder.or(
                                    criteriaBuilder.equal(root.get("currentEmployerId"), id),
                                    criteriaBuilder.equal(root.get("employerId"), id)
                            ));
                }
                predicates.add(criteriaBuilder.or(employerPredicates.toArray(new Predicate[0])));
            }

            if(visibleTeamIds != null){

                List<Predicate> visibleTeamsPredicates = new ArrayList<>();

                for(Long id: visibleTeamIds){
                    visibleTeamsPredicates.add(criteriaBuilder.equal(root.get("teamId"), id));
                }
                visibleTeamsPredicates.add(criteriaBuilder.isNull(root.get("teamId")));
                predicates.add(criteriaBuilder.or(visibleTeamsPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public Page<Unit> search(Pageable pageable){
        return null;
    }

    public List<Unit> findAllWithExpiredEld(Long waitingForEld){
        return unitRepository.findAllWithExpiredEld(waitingForEld);
    }

    public Unit detachTripAndSetReady(Long unitId){
        if(unitRepository.existsById(unitId)) {
            Unit unit = unitRepository.getOne(unitId);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -11);
            Long currentTime = calendar.getTimeInMillis();
            unit.setReadyFrom(currentTime);
            unit.setLastCompletedTripId(unit.getLastTripId());
            unit.setLastTripId(null);
            unit.setUnitStatusId(3L);
            return unitRepository.save(unit);
        }else return null;
    }

    public Map<String, Object> getUnitsLoad(Long start, Long end, List<Long> visibleCompanyIds, List<Long> visibleTeamIds){
        Map<String, Object> map = new HashMap<>();

        UnitDto dto = new UnitDto();
        dto.setVisibleTeamIds(visibleTeamIds);
        dto.setVisibleIds(visibleCompanyIds);

        List<Unit> units = unitRepository.findAll(getFilteringSpecification(dto));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        List<Long> timePoints = createTimeSequence(start, end);
        map.put("tableHeaders", timePoints);
        Map<String, Object> contentMap = new HashMap<>();
        for(Unit unit: units) {
            List<Load> loads = loadRepository.findAll(getLoadFilteringSpecification(start, end, unit.getId()), Sort.by(Sort.Direction.ASC, "centralStartTime"));

            List<Integer> coveredList = new ArrayList<>(timePoints.size());
            assertValuesToList(coveredList, 0, timePoints.size());

            for (Load load : loads) {
                System.out.printf("Number: %s, central start time: %s, end time: %s, load number: %s\n", unit.getNumber(),
                        simpleDateFormat.format(new Date(load.getCentralStartTime())), simpleDateFormat.format(new Date(load.getCentralEndTime())), load.getCustomLoadNumber());

                Long startTime = load.getCentralStartTime();
                Long endTime = load.getCentralEndTime();
                Pair<Integer, Integer> pair = getKeys(timePoints, startTime, endTime);
                setValues(coveredList, pair);
            }

            contentMap.put(unit.getNumber(), coveredList);
        }
        map.put("content", contentMap);

        return map;
    }

    private Specification<Load> getLoadFilteringSpecification(@NotNull Long start, @NotNull Long end, @NotNull Long unitId){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.between(root.get("centralStartTime"), start, end),
                    criteriaBuilder.between(root.get("centralEndTime"), start, end)
                )
            );

            predicates.add(criteriaBuilder.equal(root.get("truckId"), unitId));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private List<Long> createTimeSequence(Long start, Long end){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(calendar.getTimeInMillis() < start){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        List<Long> timePoints = new ArrayList<>();

        while(calendar.getTimeInMillis() < end){
            timePoints.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return timePoints;
    }

    private Pair<Integer, Integer> getKeys(List<Long> timePoints, Long start, Long end){
        int coveredFrom = -1, coveredTo = -1;

        for(int i = 0; i < timePoints.size(); i++){
            if(timePoints.get(i) > start)
                coveredFrom = i;
            if(timePoints.get(i) < end)
                coveredTo = i;
        }

        return Pair.of(coveredFrom, coveredTo);
    }

    private void assertValuesToList(List<Integer> list, Integer value, Integer times){
        for(int i = 0; i < times; i++){
            list.add(i, value);
        }
    }

    private void setValues(List<Integer> list, Pair<Integer, Integer> pair){
        if(pair.getLeft() != -1)
            list.set(pair.getLeft(), 1);
        if(pair.getRight() != -1)
            list.set(pair.getRight(), 1);
    }

}
