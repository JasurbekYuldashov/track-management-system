package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.DriverDto;
import uz.binart.trackmanagementsystem.dto.DriverUnitDto;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.Driver;
import uz.binart.trackmanagementsystem.model.Unit;
import uz.binart.trackmanagementsystem.model.status.DriverStatus;
import uz.binart.trackmanagementsystem.model.type.DriverType;
import uz.binart.trackmanagementsystem.repository.DriverRepository;
import uz.binart.trackmanagementsystem.repository.UnitRepository;
import uz.binart.trackmanagementsystem.repository.type.DriverTypeRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.DriverStatusService;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final FileService fileService;
    private final DriverTypeRepository driverTypeRepository;
    private final DriverStatusService driverStatusService;
    private final UnitRepository unitRepository;

    public List<Driver> findAll(){
        return driverRepository.findAll();
    }

    public Driver save(Driver driver){
        return driverRepository.save(driver);
    }

    public Driver save(DriverDto driverDto, Long userId){
        return null;
    }

    public Driver save(Driver driver, Long userId){

        driver.setId(null);
        driver.setIsActive(true);

        sequenceService.updateSequence("drivers");

        if(driver.getFirstName() != null){
            driver.setFirstName(driver.getFirstName().toUpperCase());
        }

        if(driver.getLastName() != null){
            driver.setLastName(driver.getLastName().toUpperCase());
        }

        setEmployerIdToDriverUsingHisTruckId(driver);

        Driver savedDriver = driverRepository.save(driver);
        actionService.captureCreate(savedDriver, "drivers", userId);


        return savedDriver;
    }

    public List<Driver> findByTruckId(Long truckId){
        return driverRepository.findAllByTruckId(truckId);
    }

    public Page<Driver> findFiltered(DriverDto driverDto, Pageable pageable){
        return driverRepository.findAll(getFilteringSpecification(driverDto), pageable);
    }

    public Driver findById(Long id){
        if(!driverRepository.existsById(id))
            log.warn("unable to find driver with " + id + " id");

        return driverRepository.getOne(id);
    }

    public Page<Driver> findFiltered(Long currentEmployeeId, Pageable pageable){
        return driverRepository.findAll(getFilteringSpecification(currentEmployeeId), pageable);
    }


    public Boolean existsById(Long id){
        return driverRepository.existsById(id);
    }

    public void deleteByIdAndUserId(Long id, Long userId){
        if(!driverRepository.existsById(id))
            throw new NotFoundException();
        Driver driver = driverRepository.getOne(id);
        actionService.captureDelete(driver, "drivers", userId);
        driverRepository.deleteById(id);
    }

    public void deleteById(Long id, Long userId){
        Driver driver = driverRepository.getOne(id);
        actionService.captureDelete(driver, "drivers", userId);
        driver.setDeleted(true);
        driverRepository.save(driver);
    }

    public List<Driver> getAll(){
        return driverRepository.findAll();
    }

    public DriverType getDriverTypeById(Long id){
        return driverTypeRepository.getOne(id);
    }

    public void update(Driver driver){
        if(driver.getFirstName() != null){
            driver.setFirstName(driver.getFirstName().toUpperCase());
        }
        if(driver.getLastName() != null){
            driver.setLastName(driver.getLastName().toUpperCase());
        }
        driverRepository.save(driver);
    }

    public List<Driver> findNotIn(Set<Long> ids){
        return driverRepository.findAll(notInSpecification(ids));
    }

    private Specification<Driver> getFilteringSpecification(DriverDto driverDto){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(11);

            driverDto.setSearchNameText(StringUtils.trimToNull(driverDto.getSearchNameText()));
            if(driverDto.getSearchNameText() != null){
                driverDto.setSearchNameText(driverDto.getSearchNameText().replaceAll("\\s+", " "));
                String[] names = driverDto.getSearchNameText().split(" ");

                List<Predicate> namePredicates = new ArrayList<>(names.length);

                for(String name: names){
                    namePredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%"));
                    namePredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + name.toLowerCase() + "%"));
                }
                predicates.add(criteriaBuilder.or(namePredicates.toArray(new Predicate[0])));
            }


            if(driverDto.getFirstName() != null)
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + driverDto.getFirstName() + "%"));

            if(driverDto.getLastName() != null)
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + driverDto.getLastName() + "%"));

            if(driverDto.getStreet() != null)
                predicates.add(criteriaBuilder.like(root.get("street"), "%" + driverDto.getStreet() + "%"));

            if(driverDto.getStateProvinceId() != null)
                predicates.add(criteriaBuilder.equal(root.get("stateProvinceId"), driverDto.getStateProvinceId()));

            if(driverDto.getZipCode() != null)
                predicates.add(criteriaBuilder.like(root.get("zipCode"),"%" + driverDto.getZipCode() + "%"));

            if(driverDto.getPhone() != null)
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + driverDto.getPhone() + "%"));

            if(driverDto.getAlternatePhone() != null)
                predicates.add(criteriaBuilder.like(root.get("alternatePhone"),"%" + driverDto.getAlternatePhone() + "%"));

            if(driverDto.getFax() != null)
                predicates.add(criteriaBuilder.like(root.get("fax"), "%" + driverDto.getFax() + "%"));

            if(driverDto.getEmail() != null)
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + driverDto.getEmail() + "%"));

            if(driverDto.getDefaultPaymentTypeId() != null)
                predicates.add(criteriaBuilder.equal(root.get("defaultPaymentTypeId"), driverDto.getDefaultPaymentTypeId()));

            if(driverDto.getLicenseNumber() != null)
                predicates.add(criteriaBuilder.like(root.get("licenseNumber"), "%" + driverDto.getLicenseNumber() + "%"));

            if(driverDto.getLicenseIssuedJurisdictionId() != null)
                predicates.add(criteriaBuilder.equal(root.get("licenseIssuedJurisdictionId"), driverDto.getLicenseIssuedJurisdictionId()));

            if(driverDto.getIsActive() != null)
                predicates.add(criteriaBuilder.equal(root.get("isActive"), driverDto.getIsActive()));

            if(driverDto.getVisibleIds() != null){
                List<Predicate> visibilityPredicates = new ArrayList<>();

                for(Long id: driverDto.getVisibleIds()){
                    visibilityPredicates.add(criteriaBuilder.equal(root.get("employerId"), id));
                }
                visibilityPredicates.add(criteriaBuilder.isNull(root.get("employerId")));
                predicates.add(criteriaBuilder.or(visibilityPredicates.toArray(new Predicate[0])));
            }

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Specification<Driver> notInSpecification(Set<Long> ids){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.not(root.get("id").in(ids));
            return p;
        });
    }

    public Driver updateDriversDriverType(Driver driver, Long teammate_id, Long userId){

        if(teammate_id == null && isTeamType(driver.getDriverTypeId())) {
            driver.setDriverTypeId(driver.getDriverTypeId() - 1);
            return driverRepository.save(driver);
        }

        if(teammate_id == null){
            return driver;
        }

        Optional<Driver> driverOpt = driverRepository.findById(teammate_id);

        if(!driverOpt.isPresent())
            throw new WrongEntityStructureException();

        Driver teammate = driverOpt.get();

        if(!isTeamType(driver.getDriverTypeId())){
            Long newType = driver.getDriverTypeId() + 1;
            driver.setDriverTypeId(newType);
            teammate.setDriverTypeId(newType);
            driver.setTeammateId(teammate.getId());
            teammate.setTeammateId(driver.getId());
            driverRepository.save(driver);
        }else {
            teammate.setDriverTypeId(driver.getDriverTypeId());
        }

        driverRepository.save(teammate);

        return driver;
    }

    public Boolean isTeamType(Long driverTypeId){
        return driverTypeId > 0 && driverTypeId % 2 == 0;
    }

    public String resolveName(Driver driver){
        if(driver.getFirstName() != null){
            return driver.getFirstName() + " " + driver.getLastName();
        }else return driver.getLastName();
    }

    public Specification<Driver> getFilteringSpecification(Long currentEmployerId){
        return ((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(currentEmployerId != null)
                predicates.add(criteriaBuilder.equal(root.get("currentEmployerId"), currentEmployerId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

    }

    public void updateDriver(DriverDto driverDto, Driver newDriver, Long userId){
        Driver oldDriver = findById(driverDto.getId());

        if(driverDto.getLicenseFileId() == null || oldDriver.getLicenseFileId() != null && !driverDto.getLicenseFileId().equals(oldDriver.getLicenseFileId())){
            fileService.deleteById(oldDriver.getLicenseFileId(), userId);
        }

        if(driverDto.getMedicalCardFileId() == null || oldDriver.getMedicalCardFileId()  != null && !driverDto.getMedicalCardFileId() .equals(oldDriver.getMedicalCardFileId())){
            fileService.deleteById(oldDriver.getMedicalCardFileId(), userId);
        }

        if(driverDto.getCustomFileId() == null || oldDriver.getCustomFileId()  != null && !driverDto.getCustomFileId() .equals(oldDriver.getCustomFileId())){
            fileService.deleteById(oldDriver.getCustomFileId(), userId);
        }

        setEmployerIdToDriverUsingHisTruckId(newDriver);

        actionService.captureUpdate(oldDriver, newDriver, "drivers", userId);
        driverRepository.save(newDriver);
    }

    public void updateStatus(Long driverId, Long statusId, Long userId){

        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        Optional<DriverStatus> driverStatusOpt = driverStatusService.findById(statusId);

        if(!driverOpt.isPresent() || !driverStatusOpt.isPresent())
            throw new NotFoundException();

        Driver oldDriver = driverOpt.get();
        Driver newDriver = oldDriver;

        newDriver.setDriverStatusId(statusId);

        actionService.captureUpdate(oldDriver, newDriver, "drivers", userId);

        driverRepository.save(newDriver);
    }

    public Driver getByTruckId(Long truckId){
        return driverRepository.findFirstByTruckId(truckId);
    }

    public List<Object[]> findAllForUnitDto(){
        return driverRepository.findAllForContext();
    }

    private void setEmployerIdToDriverUsingHisTruckId(Driver driver){
        if(driver.getTruckId() != null){
            if(unitRepository.existsById(driver.getTruckId())){
                Unit unit = unitRepository.getOne(driver.getTruckId());
                driver.setEmployerId(unit.getEmployerId());
            }
        }
    }

}
