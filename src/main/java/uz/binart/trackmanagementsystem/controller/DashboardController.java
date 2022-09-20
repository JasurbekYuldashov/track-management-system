package uz.binart.trackmanagementsystem.controller;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uz.binart.trackmanagementsystem.dto.*;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;
import uz.binart.trackmanagementsystem.model.type.OwnershipType;
import uz.binart.trackmanagementsystem.model.type.UnitType;
import uz.binart.trackmanagementsystem.repository.LoadRepository;
import uz.binart.trackmanagementsystem.repository.TripRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.impl.AccountingServiceImpl;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;
import uz.binart.trackmanagementsystem.service.type.OwnershipTypeService;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final TripRepository tripRepository;
    private final LoadRepository loadRepository;

    private final TripService tripService;
    private final AccountingServiceImpl accountingService;
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

    @Autowired
    private RestTemplate restTemplate;

    private static Date trim(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        return calendar.getTime();
    }

//    private Long getLastSaturday() {
//
//        LocalDate CurrentDate = LocalDate.now();
//
//        YearMonth ym = YearMonth.of(CurrentDate.getYear(), CurrentDate.getMonth());
//        //YearMonth ym = YearMonth.of(2018, 9);
//        LocalDate endDate = ym.atEndOfMonth();
//
//        DayOfWeek day = endDate.getDayOfWeek();
//        int lastDay = day.getValue();
//
//        System.out.print("The last Saturday of the month falls on: ");
//
//        if (lastDay < 6)
//            endDate = endDate.minusDays(lastDay + 1);
//        else if (lastDay > 6)
//            endDate = endDate.minusDays(1);
//
//        System.out.println(endDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//        Date date = null;
//        try {
//            date = sdf.parse(String.valueOf(endDate));
//        } catch (ParseException e) {
////            throw new RuntimeException(e);
//        }
////        date.setTime(0);
////        date.setHours(0);
////        date.setMinutes(0);
////        date.setSeconds(0);
//        long millis = trim(date).getTime();
//        return millis;
//    };

    @GetMapping("/context")
    private ResponseEntity<?> getContext() {
        Map<String, List<Team>> teams = new HashMap<>();
        List<Long> visibleTeams = utilService.getVisibleTeamIds(userService.getCurrentUserFromContext());
        teams.put("teams", teamService.findVisible(visibleTeams));
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/dashboardData")
    private ResponseEntity<Map<String, Object>> getDashBoardData() {
        Map<String, Object> map = new HashMap<>();
        map.put("teams", teamService.findAll());
        map.put("companies", ownedCompanyService.getAll());
        return ResponseEntity.ok(map);
    }

    @GetMapping
    public Map<String, Object> dashboard(
            @RequestParam(name = "currentEmployerId", required = false) Long currentEmployerId,
            @RequestParam(name = "teamId", required = false) Long teamId,
            Pageable pageable,
            @RequestParam(name = "startTime") Long startTime,
            @RequestParam(name = "endTime") Long endTime
    ) throws JSONException {
        System.out.println(teamId);

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
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
//        calendar.add(Calendar.HOUR_OF_DAY, 0);
//        Date date = new Date();
//        Long currentTime = date.getTime();
//        Long lastSaturday = getLastSaturday();
        Long minusTime = endTime - startTime;

        for (int i = 0; i < unitDtoList.size(); i++) {
            UnitDashboardDto asd = unitDtoList.get(i);
//            tripRepository.getAll();
            ;
//            a.add(Long.valueOf(26803));
//            if (asd.getTruckId() != null) {
////                String s1 = tripRepository.test111(asd.getTruckId());
//
//            }
            System.out.println(asd.getEndTime());
            try {
                if (asd.getTruckId() != null) {
//                    List<String> ids = tripRepository.test1(asd.getTruckId());
//                    List<Load> loadss = loadRepository.testatssad(ids.stream().map(Long::parseLong).collect(Collectors.toList()), lastSaturday, currentTime);
//                    Calendar start = Calendar.getInstance();
//                    Calendar end = Calendar.getInstance();
//
//                    start.setTime(new Date(currentTime));
//                    end.setTime(new Date(lastSaturday));
                    List<AccountingDto> loadss = accountingService.getProperInfo(null, asd.getTruckId(), null, null, null, startTime, endTime, true, false);
                    float a = 0;
                    for (AccountingDto accountingDto : loadss) {
                        try {
                            for (int k = 0; k < accountingDto.getSegmentedPrices().length; k++) {
                                a += accountingDto.getSegmentedPrices()[k];
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    asd.setCalc(a);
                    asd.setCalc1(a);
                    unitDtoList.set(i, asd);
                }
            } catch (Exception ignored) {
            }
        }
        map.put("data", unitDtoList);

        return map;
    }

    List<UnitDashboardDto> packToDto(List<Unit> units) throws JSONException {

        List<UnitDashboardDto> unitDtoList = new ArrayList<>();

        for (Unit unit : units) {
            UnitDashboardDto dto = new UnitDashboardDto();
            dto.setTruckId(unit.getId());
            dto.setNumber(unit.getNumber());

            UnitType unitType = unitTypeService.getFromManualCache(unit.getUnitTypeId());
            if (unitType != null) {
                dto.setTypeOfUnit(unitType.getName());
            }

            if (unit.getNotes() != null) {
                dto.setNotes(unit.getNotes());
            } else dto.setNotes("");

            dto.setUnitStatusId(unit.getUnitStatusId());

            UnitStatus unitStatus = unitStatusService.getFromManualCache(dto.getUnitStatusId());

            if (unitStatus != null) {
                dto.setUnitStatus(unitStatus.getName());
                dto.setUnitStatusColor(unitStatus.getColor());
            }

            if (unit.getDriverId() != null) {
                Driver driver = driverService.findById(unit.getDriverId());
                dto.setDriverOneId(driver.getId());
                dto.setDriverOne(driverService.resolveName(driver));
                dto.setDriverOnePhoneNumber(driver.getPhone() != null ? driver.getPhone() : "fuck");
            }
            if (unit.getSecondDriverId() != null) {
                Driver secondDriver = driverService.findById(unit.getSecondDriverId());
                dto.setDriverTwoId(secondDriver.getId());
                dto.setDriverTwo(driverService.resolveName(secondDriver));
                dto.setDriverTwoPhoneNumber(secondDriver.getPhone() != null ? secondDriver.getPhone() : "fuck");
            }

            if (unit.getOwnershipTypeId() != null) {
                OwnershipType ownershipType = ownerShipTypeService.getOne(unit.getOwnershipTypeId());
                dto.setTypeOfDriver(ownershipType.getAbbreviation());
            }


            Trip trip = null;
            if (unit.getLastTripId() != null) {
                Optional<Trip> tripOptional = tripService.getById(unit.getLastTripId());
                if (!tripOptional.isEmpty()) {
                    trip = tripOptional.get();
                }

            }

            if (trip != null && trip.getActiveLoadId() != null) {
                Load load = loadService.findById(trip.getActiveLoadId());


                dto.setLoadNumber(load.getCustomLoadNumber());
                dto.setLoadId(load.getId());

                List<Long> sortedStopIds = load.getSortedPickupAndDeliveryIds();
                if (sortedStopIds != null && sortedStopIds.size() >= 2) {
                    Pair<PickupDto, DeliveryDto> pair = utilService.getFirstAndLastStopsOnlyLocations(sortedStopIds.get(0), sortedStopIds.get(sortedStopIds.size() - 1));
                    PickupDto pickupDto = pair.getFirst();
                    DeliveryDto deliveryDto = pair.getSecond();

                    Company company = companyService.getById(deliveryDto.getConsigneeCompanyId());

                    String correctedViaCentralTimeZoneTime = resolveTime(deliveryDto.getDeliveryDate(), company);
                    dto.setFrom(pickupDto.getConsigneeNameAndLocation());
                    dto.setTo(deliveryDto.getConsigneeNameAndLocation());
                    System.out.println(correctedViaCentralTimeZoneTime);
                    dto.setEndTime(correctedViaCentralTimeZoneTime);
                }
                dto.setTripId(trip.getId());

            } else if (dto.getUnitStatusId().equals(3L) && unit.getReadyFrom() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, -11);
//                Long currentTime = calendar.getTimeInMillis();

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

                HttpEntity<String> entity = new HttpEntity<String>(headers);

                ResponseEntity<String> time = restTemplate.exchange("https://timeapi.io/api/Time/current/zone?timeZone=America/Chicago", HttpMethod.GET, entity, String.class);
                JSONObject jsonObject = new JSONObject(time.getBody());
//        System.out.println(time.getBody());

                LocalDateTime dateTime = LocalDateTime.parse(jsonObject.get("dateTime").toString());
                Timestamp timestamp = Timestamp.valueOf(dateTime);

//        System.out.println(timestamp.getTime());
                Long currentTime = timestamp.getTime();

                long numberOfHours = (currentTime - unit.getReadyFrom()) / 3600000L;

                dto.setEndTime(numberOfHours + " HOURS");

                if (unit.getLastCompletedTripId() != null)
                    dto.setTo(defineLastTripEndingLocationForReadyUnits(unit.getLastCompletedTripId()));

            } else if (dto.getUnitStatusId().equals(7L) && unit.getEldUnTil() != null) {
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

    private String resolveTime(Date date, Company company) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer timeZone = -6;
        Integer central = -6;
        if (company.getLocationId() != null) {

            Location location = locationService.findById(company.getLocationId());
            if (location != null) {
                Location parentLocation = locationService.findByAnsiFromCache(location.getParentAnsi());
                if (parentLocation != null) {

                    if (location.getParentTimeZone().equals(1))
                        timeZone = parentLocation.getFirstTimeZone();
                    else if (location.getParentTimeZone().equals(2))
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
        } else return "";
    }


}
