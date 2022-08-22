package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.DriverDto;
import uz.binart.trackmanagementsystem.dto.DriverUnitDto;
import uz.binart.trackmanagementsystem.model.Driver;
import uz.binart.trackmanagementsystem.model.type.DriverType;

import java.util.List;
import java.util.Set;

public interface DriverService {

    List<Driver> findAll();

    Driver save(Driver driver);

    Driver save(DriverDto driverDto, Long userId);

    Driver save(Driver driver, Long userId);

    List<Driver> findByTruckId(Long truckId);

    Page<Driver> findFiltered(DriverDto driverDto, Pageable pageable);

    Driver findById(Long id);

    Page<Driver> findFiltered(Long currentEmployeeId, Pageable pageable);

    Boolean existsById(Long id);

    void deleteByIdAndUserId(Long id, Long userId);

    void deleteById(Long id, Long userId);

    List<Driver> getAll();

    DriverType getDriverTypeById(Long id);

    void update(Driver driver);

    List<Driver> findNotIn(Set<Long> ids);

    Driver updateDriversDriverType(Driver driver, Long teammate_id, Long userId);

    Boolean isTeamType(Long driverTypeId);

    String resolveName(Driver driver);

    void updateDriver(DriverDto driverDto, Driver newDriver, Long userId);

    void updateStatus(Long driverId, Long statusId, Long userId);

    Driver getByTruckId(Long truckId);

    List<Object[]> findAllForUnitDto();

}
