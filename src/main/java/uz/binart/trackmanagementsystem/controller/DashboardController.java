package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.*;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;
import uz.binart.trackmanagementsystem.model.type.OwnershipType;
import uz.binart.trackmanagementsystem.model.type.UnitType;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;
import uz.binart.trackmanagementsystem.service.type.OwnershipTypeService;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final TripService tripService;
    private final LoadService loadService;
    private final DriverService driverService;
    private final UnitService unitService;
    private final OwnedCompanyService ownedCompanyService;
    private final UnitTypeService unitTypeService;
    private final UnitStatusService unitStatusService;
    private final UserService userService;
    private final CompanyService companyService;
    private final LocationService locationService;
    private final UtilService utilService;
    private final TeamService teamService;
    private final OwnershipTypeService ownerShipTypeService;

    @GetMapping("/context")
    private ResponseEntity<?> getContext(){
        Map<String, List<Team>> teams = new HashMap<>();
        List<Long> visibleTeams = utilService.getVisibleTeamIds(userService.getCurrentUserFromContext());
        teams.put("teams", teamService.findVisible(visibleTeams));
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/dashboardData")
    private ResponseEntity<Map<String, Object>> getDashBoardData(){
        Map<String, Object> map = new HashMap<>();
        map.put("teams", teamService.findAll());
        map.put("companies", ownedCompanyService.getAll());
        return ResponseEntity.ok(map);
    }

    @GetMapping
    public Map<String, Object> dashboard(@RequestParam(name = "currentEmployerId", required = false)Long currentEmployerId, @RequestParam(name = "teamId", required = false)Long teamId, Pageable pageable){

        UnitDto unitDto = new UnitDto();

        User user = userService.getCurrentUserFromContext();

        List<Long> visibleIds = utilService.getVisibleIds(user);
        List<Long> visibleTeamIds = utilService.getVisibleTeamIds(user);

        unitDto.setCurrentEmployerId(currentEmployerId);
        unitDto.setEmployerId(currentEmployerId);
        unitDto.setTeamId(teamId);
        unitDto.setVisibleIds(visibleIds);
        unitDto.setVisibleTeamIds(visibleTeamIds);

        unitDto.setUnitStatusId(3L);
        Page<Unit> ready = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(2L);
        Page<Unit> upcoming = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(1L);
        Page<Unit> covered = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(4L);
        Page<Unit> noDriver = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(5L);
        Page<Unit> repair = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(7L);
        Page<Unit> waitingForEld = unitService.findFiltered(unitDto, pageable);

        unitDto.setUnitStatusId(8L);
        Page<Unit> atHome = unitService.findFiltered(unitDto, pageable);

        List<UnitDashboardDto> unitDtoList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        unitDtoList.addAll(packToDto(ready.getContent()));
        unitDtoList.addAll(packToDto(waitingForEld.getContent()));
        unitDtoList.addAll(packToDto(upcoming.getContent()));
        unitDtoList.addAll(packToDto(covered.getContent()));
        unitDtoList.addAll(packToDto(atHome.getContent()));
        unitDtoList.addAll(packToDto(repair.getContent()));
        unitDtoList.addAll(packToDto(noDriver.getContent()));

        map.put("data", unitDtoList);

        return map;
    }

    List<UnitDashboardDto> packToDto(List<Unit> units){

        List<UnitDashboardDto> unitDtoList = new ArrayList<>();

        for(Unit unit: units){
            UnitDashboardDto dto = new UnitDashboardDto();
            dto.setTruckId(unit.getId());
            dto.setNumber(unit.getNumber());

          //  Driver driver = driverService.getByTruckId(unit.getId());

            UnitType unitType = unitTypeService.getFromManualCache(unit.getUnitTypeId());
            if(unitType != null) {
                dto.setTypeOfUnit(unitType.getName());
            }

            if(unit.getNotes() != null){
                dto.setNotes(unit.getNotes());
            }else dto.setNotes("");

            dto.setUnitStatusId(unit.getUnitStatusId());

            UnitStatus unitStatus = unitStatusService.getFromManualCache(dto.getUnitStatusId());

            if(unitStatus != null) {
                dto.setUnitStatus(unitStatus.getName());
                dto.setUnitStatusColor(unitStatus.getColor());
            }

            if(unit.getDriverId() != null){
                Driver driver = driverService.findById(unit.getDriverId());
                dto.setDriverOneId(driver.getId());
                dto.setDriverOne(driverService.resolveName(driver));
                dto.setDriverOnePhoneNumber(driver.getPhone() != null ? driver.getPhone() : "fuck");
            }
            if(unit.getSecondDriverId() != null){
                Driver secondDriver = driverService.findById(unit.getSecondDriverId());
                dto.setDriverTwoId(secondDriver.getId());
                dto.setDriverTwo(driverService.resolveName(secondDriver));
                dto.setDriverTwoPhoneNumber(secondDriver.getPhone() != null ? secondDriver.getPhone() : "fuck");
            }

            if(unit.getOwnershipTypeId() != null){
                OwnershipType ownershipType = ownerShipTypeService.getOne(unit.getOwnershipTypeId());
                dto.setTypeOfDriver(ownershipType.getAbbreviation());
            }


            Trip trip = null;
            if(unit.getLastTripId() != null) {
                Optional<Trip> tripOptional = tripService.getById(unit.getLastTripId());
                if(!tripOptional.isEmpty()){
                    trip = tripOptional.get();
                }

            }

            if(trip != null && trip.getActiveLoadId() != null){
                Load load = loadService.findById(trip.getActiveLoadId());


                dto.setLoadNumber(load.getCustomLoadNumber());
                dto.setLoadId(load.getId());

                List<Long> sortedStopIds = load.getSortedPickupAndDeliveryIds();
                if(sortedStopIds != null && sortedStopIds.size() >= 2){
                    Pair<PickupDto, DeliveryDto> pair = utilService.getFirstAndLastStopsOnlyLocations(sortedStopIds.get(0), sortedStopIds.get(sortedStopIds.size() - 1));
                    PickupDto pickupDto = pair.getFirst();
                    DeliveryDto deliveryDto = pair.getSecond();

                    Company company = companyService.getById(deliveryDto.getConsigneeCompanyId());

                    String correctedViaCentralTimeZoneTime = resolveTime(deliveryDto.getDeliveryDate(), company);
                    dto.setFrom(pickupDto.getConsigneeNameAndLocation());
                    dto.setTo(deliveryDto.getConsigneeNameAndLocation());
                    dto.setEndTime(correctedViaCentralTimeZoneTime);
                }
                dto.setTripId(trip.getId());

            }else if(dto.getUnitStatusId().equals(3L) && unit.getReadyFrom() != null){
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, -11);
                Long currentTime = calendar.getTimeInMillis();

                long numberOfHours = (currentTime - unit.getReadyFrom()) / 3600000L;

                dto.setEndTime(numberOfHours + " HOURS");

                if(unit.getLastCompletedTripId() != null)
                    dto.setTo(defineLastTripEndingLocationForReadyUnits(unit.getLastCompletedTripId()));

            }else if(dto.getUnitStatusId().equals(7L) && unit.getEldUnTil() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
                Date result = new Date(unit.getEldUnTil());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(result);
                calendar.add(Calendar.HOUR_OF_DAY, -10);
                dto.setEndTime(simpleDateFormat.format(calendar.getTime()));
            }

            dto.setTeamColor(teamService.getTeamColor(unit.getTeamId()));
            unitDtoList.add(dto);
        }

        return unitDtoList;
    }

    private String resolveTime(Date date, Company company){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer timeZone = -6;
        Integer central = -6;
        if(company.getLocationId() != null){

            Location location = locationService.findById(company.getLocationId());
            if(location != null){
                Location parentLocation = locationService.findByAnsiFromCache(location.getParentAnsi());
                if(parentLocation != null){

                    if(location.getParentTimeZone().equals(1))
                        timeZone = parentLocation.getFirstTimeZone();
                    else if(location.getParentTimeZone().equals(2))
                        timeZone = parentLocation.getSecondTimeZone();
                }

            }
        }

        int dif = central - timeZone;

        calendar.add(Calendar.HOUR_OF_DAY, dif);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");

        Date result = new Date(calendar.getTimeInMillis());
        return simpleDateFormat.format(result);

    }

    public String defineLastTripEndingLocationForReadyUnits(Long lastCompletedTripId) {

        Optional<Trip> tripOptional = tripService.getById(lastCompletedTripId);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();
            if (trip.getActiveLoadId() != null) {
                Load load = loadService.findById(trip.getActiveLoadId());
                List<Long> sortedStopIds = load.getSortedPickupAndDeliveryIds();
                if (sortedStopIds != null && sortedStopIds.size() >= 2) {
                    Pair<PickupDto, DeliveryDto> pair = utilService.getFirstAndLastStops(sortedStopIds.get(0), sortedStopIds.get(sortedStopIds.size() - 1));
                    DeliveryDto deliveryDto = pair.getSecond();
                    Company company = companyService.getById(deliveryDto.getConsigneeCompanyId());
                    return utilService.resolveLocationNameAndParentAbbreviation(company.getLocationId());
                } else return "";

            } else {
                return "";
            }
        }
        else return "";
    }




}
