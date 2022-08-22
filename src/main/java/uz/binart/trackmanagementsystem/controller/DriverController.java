package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uz.binart.trackmanagementsystem.dto.DriverDto;
import uz.binart.trackmanagementsystem.dto.TripDto;
import uz.binart.trackmanagementsystem.model.Driver;
import uz.binart.trackmanagementsystem.model.Trip;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.model.status.DriverStatus;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.DriverStatusService;
import uz.binart.trackmanagementsystem.service.type.PaymentTypeService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final UserService userService;
    private final PaymentTypeService paymentTypeService;
    private final TripService tripService;
    private final DriverStatusService driverStatusService;
    private final PackagingService packagingService;
    private final StateProvinceService stateProvinceService;
    private final UtilService utilService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/context")
    public ResponseEntity<?> getContext(){
        Map<String, Object> map = new HashMap<>();
        map.put("payment_types", paymentTypeService.getAll());
        map.put("driver_statuses", driverStatusService.getAll());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/new")
    public ResponseEntity<String> postNewDriver(@RequestBody @Valid DriverDto driverDto){
        Driver driver = modelMapper.map(driverDto, Driver.class);
        Long userId = userService.getCurrentUserFromContext().getId();
        driverService.save(driver, userId);
        return ResponseEntity.ok("new driver successfully created");
    }


    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getListOfDrivers(DriverDto driverDto, Pageable pageable){

        List<Long> visibleIds = utilService.getVisibleIds(userService.getCurrentUserFromContext());
        driverDto.setVisibleIds(visibleIds);

        Page<Driver> drivers = driverService.findFiltered(driverDto, pageable);
        Date currentDate = new Date();

        Map<String, Object> map = new HashMap<>();
        List<DriverDto> driverDtoS = new ArrayList<>();

        String pattern = "YYYY-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        for(Driver driver: drivers){
            DriverDto driverDto_ = modelMapper.map(driver, DriverDto.class);

            driverDto_.setAddress(stateProvinceService.getById(driver.getStateProvinceId()).getName());
            driverDto_.setPaymentType(paymentTypeService.getPaymentTypeName(driver.getDefaultPaymentTypeId()));
            driverDto_.setDriverStatus(driverStatusService.getFromManualCache(driver.getDriverStatusId()).getName());

            if(driver.getLicenseExpiration() != null && driver.getLicenseExpiration().compareTo(currentDate) > 0)
                driverDto_.setActive(true);
            else driverDto_.setActive(false);

            if(driver.getLicenseExpiration() != null)
                driverDto_.setLicenseExpirationFormatted(simpleDateFormat.format(driver.getLicenseExpiration()));
            else driverDto_.setLicenseExpirationFormatted("");
            if(driver.getMedicalCardRenewal() != null)
                driverDto_.setMedicalCardRenewalFormatted(simpleDateFormat.format(driver.getMedicalCardRenewal()));
            else driverDto_.setMedicalCardRenewalFormatted("");
            if(driver.getHireDate() != null)
                driverDto_.setHireDateFormatted(simpleDateFormat.format(driver.getHireDate()));
            else
                driverDto_.setHireDateFormatted("");
            if(driver.getTerminationDate() != null)
                driverDto_.setTerminationDateFormatted(simpleDateFormat.format(driver.getTerminationDate()));

            DriverStatus driverStatus = driverStatusService.getFromManualCache(driver.getDriverStatusId());

            if(driverStatus != null) {
                driverDto_.setDriverStatusColor(driverStatus.getColor());
            }
            if(driver.getFirstName() != null){
                driverDto_.setFullName(driver.getFirstName() + " " + driver.getLastName());
            }else driverDto_.setFullName(driver.getLastName());

            if(driverService.isTeamType(driver.getDriverTypeId()) && driver.getTeammateId() != null){
                Driver teammate = driverService.findById(driver.getTeammateId());
                if(teammate.getFirstName() != null){
                    driverDto_.setTeammateFullName(teammate.getFirstName() + " " + teammate.getLastName());
                }else driverDto_.setTeammateFullName(teammate.getLastName());

            }

            driverDtoS.add(driverDto_);
        }

        map.put("number", drivers.getNumber());
        map.put("totalPages", drivers.getTotalPages());
        map.put("totalElements", drivers.getTotalElements());
        map.put("content", driverDtoS);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable @NotNull Long id){
        if(!driverService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Driver driver = driverService.findById(id);

        DriverDto driverDto = modelMapper.map(driver, DriverDto.class);

        String pattern = "YYYY-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if(driver.getDefaultPaymentTypeId() != null){
            driverDto.setPaymentType(paymentTypeService.getPaymentTypeName(driver.getDefaultPaymentTypeId()));
        }


        if(driver.getLicenseExpiration() != null)
            driverDto.setLicenseExpirationFormatted(simpleDateFormat.format(driver.getLicenseExpiration()));
        if(driver.getMedicalCardRenewal() != null)
            driverDto.setMedicalCardRenewalFormatted(simpleDateFormat.format(driver.getMedicalCardRenewal()));
        if(driver.getMedicalCardRenewal() != null)
            driverDto.setHireDateFormatted(simpleDateFormat.format(driver.getHireDate()));
        if(driver.getTerminationDate() != null)
            driverDto.setTerminationDateFormatted(simpleDateFormat.format(driver.getTerminationDate()));

        Pageable pageable = PageRequest.of(0,20, Sort.by(Sort.Direction.DESC, "id"));

        Page<Trip> trips = tripService.findFiltered(id, pageable);
        List<TripDto> tripDtoS = new ArrayList<>();

        for(Trip trip: trips){
            tripDtoS.add(packagingService.packTripToDto(trip));
        }

        driverDto.setTripDtoS(tripDtoS);

        return ResponseEntity.ok(driverDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @NotNull Long id){

        if(!driverService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no driver with such id");
        }
        User user = userService.getCurrentUserFromContext();

        driverService.deleteById(id, user.getId());

        return ResponseEntity.ok("driver was successfully deleted");
    }

    @PutMapping("/edit")
    public ResponseEntity<?> updateDriver(@RequestBody @NotNull DriverDto driverDto){
        Driver newDriver = modelMapper.map(driverDto, Driver.class);

        User user = userService.getCurrentUserFromContext();

        newDriver = modelMapper.map(driverDto, Driver.class);

        if(newDriver.getIsActive() == null) newDriver.setIsActive(true);
        if(newDriver.getDeleted() == null) newDriver.setDeleted(false);

        driverService.updateDriver(driverDto, newDriver, user.getId());

        return ResponseEntity.ok("driver was updated");
    }

    @PutMapping("/edit-status/{id}/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable @NotNull Long id, @PathVariable @NotNull Long statusId){
        User user = userService.getCurrentUserFromContext();
        driverService.updateStatus(id, statusId, user.getId());
        return ResponseEntity.ok("updated successfully");
    }

}
