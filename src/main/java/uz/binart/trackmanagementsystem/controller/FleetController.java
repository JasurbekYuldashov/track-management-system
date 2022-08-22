package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.UnitDto;
import uz.binart.trackmanagementsystem.dto.FleetDto;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.OwnedCompany;
import uz.binart.trackmanagementsystem.model.Unit;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;
import uz.binart.trackmanagementsystem.model.type.OwnershipType;
import uz.binart.trackmanagementsystem.service.DriverService;
import uz.binart.trackmanagementsystem.service.ExpirationNotificationService;
import uz.binart.trackmanagementsystem.service.OwnedCompanyService;
import uz.binart.trackmanagementsystem.service.UnitService;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;
import uz.binart.trackmanagementsystem.service.type.OwnershipTypeService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fleet")
@RequiredArgsConstructor
public class FleetController {

    private final UnitService unitService;
    private final OwnedCompanyService ownedCompanyService;
    private final DriverService driverService;
    private final OwnershipTypeService ownershipTypeService;
    private final UnitStatusService unitStatusService;
    private final ExpirationNotificationService expirationNotificationService;

    @GetMapping("/list")
    public ResponseEntity<?> getFleetList(UnitDto unitDto, @PageableDefault(page = 0, size = 20000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){

        Page<Unit> unitPage = unitService.findFiltered(unitDto, pageable);

        Map<String, Object> map = new HashMap<>();

        map.put("data", convertToFleetDtoS(unitPage.getContent()));

        return ResponseEntity.ok(map);
    }

    List<FleetDto> convertToFleetDtoS(List<Unit> units){

        List<FleetDto> result = new ArrayList<>(units.size());

        for(Unit unit: units){
            FleetDto fleet = new FleetDto();

            fleet.setId(unit.getId());

            resolveOwnedCompanyIdNameAndAbbreviation(fleet, unit);

            fleet.setNumber(unit.getNumber());

            resolveOwnershipType(fleet, unit);

            fleet.setLicenseExpiration(unit.getLicensePlateExpirationTime());

            fleet.setPmByMillage(unit.getPmByMillage());
            fleet.setPmByDate(unit.getPmByDate());

            fleet.setVinNumber(unit.getVin());

            resolveUnitStatus(fleet, unit);

            result.add(fleet);
        }

        return result;

    }

    @GetMapping("/expiration_notifications")
    public ResponseEntity<?> getExpirationNotifications(@RequestParam(name = "was_seen", required = false)Boolean onlyNotSeen, @PageableDefault(size = 20, direction = Sort.Direction.ASC, sort = "id")Pageable pageable){
        Map<String, Object> map = new HashMap<>();
        map.put("wasNotSeenCount", expirationNotificationService.totalNotSeen());
        map.put("page", expirationNotificationService.getNotifications(onlyNotSeen, pageable));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/expiration_notification/{id}")
    public ResponseEntity<?> getExpirationNotification(@PathVariable @NotNull Long id){
        try{
            return ResponseEntity.ok(expirationNotificationService.getByIdAndMarkAsSeen(id));
        }catch(NotFoundException notFoundException){
            Map<String, String> map = new HashMap<>();
            map.put("errorMessage", "such notification not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    private void resolveOwnedCompanyIdNameAndAbbreviation(FleetDto fleet, Unit unit){

        if(unit.getEmployerId() != null){
            OwnedCompany ownedCompany = ownedCompanyService.getFromCache(unit.getEmployerId());
            fleet.setOwnedCompanyId(ownedCompany.getId());
            fleet.setOwnedCompanyName(ownedCompany.getName());
            fleet.setOwnedCompanyAbbreviation(ownedCompany.getAbbreviation());
        }
        else if(unit.getCurrentEmployerId() != null){
            OwnedCompany ownedCompany = ownedCompanyService.getFromCache(unit.getCurrentEmployerId());
            fleet.setOwnedCompanyId(ownedCompany.getId());
            fleet.setOwnedCompanyName(ownedCompany.getName());
            fleet.setOwnedCompanyAbbreviation(ownedCompany.getAbbreviation());
        }else{
            fleet.setOwnedCompanyId(null);
            fleet.setOwnedCompanyName("");
            fleet.setOwnedCompanyAbbreviation("");
        }

    }

    private void resolveOwnershipType(FleetDto fleet, Unit unit){
        if(unit.getOwnershipTypeId() != null){
            OwnershipType ownershipType = ownershipTypeService.getOne(unit.getOwnershipTypeId());

            if(ownershipType != null){
                fleet.setOwnershipType(ownershipType.getName());
            }else fleet.setOwnershipType("");

        }else fleet.setOwnershipType("");
    }

    private void resolveUnitStatus(FleetDto fleet, Unit unit){
        UnitStatus unitStatus = unitStatusService.getFromManualCache(unit.getUnitStatusId());

        if(unitStatus != null) {
            fleet.setStatus(unitStatus.getName());
            fleet.setStatusColor(unitStatus.getColor());
        }else{
            fleet.setStatus("");
            fleet.setStatusColor("");
        }
    }

}
