package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.LoadDto;
import uz.binart.trackmanagementsystem.dto.TripDto;
import uz.binart.trackmanagementsystem.dto.TripForm;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.model.status.TripStatus;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.DriverStatusService;
import uz.binart.trackmanagementsystem.service.status.TripStatusService;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {

    private final DriverService driverService;
    private final UnitService unitService;
    private final TripService tripService;
    private final TripStatusService tripStatusService;
    private final UserService userService;
    private final OwnedCompanyService ownedCompanyService;
    private final DeliveryService deliveryService;
    private final PickupService pickupService;
    private final CompanyService companyService;
    private final LoadService loadService;
    private final LocationService locationService;
    private final PackagingService packagingService;
    private final DriverStatusService driverStatusService;
    private final UnitStatusService unitStatusService;
    private final UtilService utilService;
    private final ModelMapper mapper = new ModelMapper();

    @GetMapping("/context")
    public ResponseEntity getContext(){
        Map<String, Object> res = new HashMap<>();
        res.put("drivers", driverService.getAll());
        res.put("trailers", unitService.findAllByType(2L));
        res.put("owned_companies", ownedCompanyService.getAll());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/edit_context/{id}")
    public ResponseEntity<?> getContextForEdit(@PathVariable @NotNull Long id){
        Optional<Trip> tripOpt = tripService.getById(id);

        if(!tripOpt.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        Map<String, Object> map = new HashMap<>();

        Trip trip = tripOpt.get();
        List<Long> loadIds = trip.getLoadIds();

        List<LoadDto> loads = new ArrayList<>();

        for(Long loadId: loadIds){
            Load load = loadService.findById(loadId);
            LoadDto loadDto = packagingService.packLoadToDto(load);
            loads.add(loadDto);
        }

        Optional<Unit> unitOptional = unitService.findById(trip.getTruckId());

        if(unitOptional.isPresent()){
            map.put("unit_id", unitOptional.get().getId());
            map.put("unit_number", unitOptional.get().getNumber());
        }

        map.put("loads", loads);

        map.put("trip", packagingService.packTripToDto(trip));



        return ResponseEntity.ok(map);
    }


    @PostMapping("/new")
    public ResponseEntity newTrip(@Valid @RequestBody TripForm tripDto){

        Trip trip = new Trip();
        trip.setCustomTripNumber(tripDto.getCustomTripNumber());
        trip.setDriverId(tripDto.getDriverId());
        trip.setOdometer(tripDto.getOdometer());
        trip.setTruckId(tripDto.getTruckId());
        trip.setDriverInstructions(tripDto.getDriverInstructions());
        if(tripDto.getSecondDriverId() != null)
            trip.setSecondDriverId(tripDto.getSecondDriverId());
        trip.setLoadIds(tripDto.getLoadIds());

        Long userId = userService.getCurrentUserFromContext().getId();
        Driver driver = driverService.findById(tripDto.getDriverId());
        driverService.updateDriversDriverType(driver, tripDto.getSecondDriverId(), userId);

        tripService.save(trip, userId);

        return ResponseEntity.ok("trip created successfully");
    }

    @PutMapping
    public ResponseEntity editTrip(@Valid @RequestBody TripForm tripDto){
        if(tripDto.getId() == null)
            return ResponseEntity.badRequest().body("id is null, impossible to update");

        tripService.updateTrip(tripDto);

        return ResponseEntity.ok().body("updated successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDto> getById(@PathVariable @NotNull Long id){
        Optional<Trip> tripOpt = tripService.getById(id);

        if(tripOpt.isPresent()){
            Trip trip = tripOpt.get();
            TripDto tripDto = mapper.map(trip, TripDto.class);
            Optional<OwnedCompany> ownedCompanyOptional = ownedCompanyService.findById(tripDto.getOwnedCompanyId());
            if(ownedCompanyOptional.isPresent())
                tripDto.setOwnedCompanyName(ownedCompanyOptional.get().getName());
            if(trip.getActiveLoadId() != null){
                Load load =  loadService.findById(trip.getActiveLoadId());
                Pair<List<Long>, List<Long>> pickupsAndDeliveries = loadService.getAllPickupsByTripId(trip.getId());
                tripDto.setChronologicalSequence(utilService.sortByPickupOrDeliveryDate(pickupsAndDeliveries.getFirst(), pickupsAndDeliveries.getSecond(), false));

                tripDto.setCustomerName(companyService.getById(load.getCustomerId()).getCompanyName());
                Driver driver = driverService.findById(trip.getDriverId());

                tripDto.setDriverName(driverService.resolveName(driver));

                if(trip.getSecondDriverId() != null) {
                    tripDto.setTeammateId(trip.getSecondDriverId());
                    tripDto.setTeammateName(driverService.resolveName(driverService.findById(trip.getSecondDriverId())));
                }

                Unit unit = unitService.getById(trip.getTruckId());
                tripDto.setUnitNumber(unit.getNumber());
                tripDto.setUnitId(trip.getTruckId());
                tripDto.setDriverStatusName(driverStatusService.findById(driver.getDriverStatusId()).get().getName());
                tripDto.setUnitStatusName(unitStatusService.findById(unit.getUnitStatusId()).getName());
            }

            if(trip.getLoadIds() != null) {
                List<Long> loadIds = trip.getLoadIds();
                tripDto.setLoadDtoList(new ArrayList<>());
                for(Long id_: loadIds){
                    Load load = loadService.findById(id_);
                    LoadDto loadDto = packagingService.loadToDtoSingle(load);
                    tripDto.getLoadDtoList().add(loadDto);
                }
            }

            return ResponseEntity.ok(tripDto);
        }else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable @NotNull Long id){
        Long userId = userService.getCurrentUserFromContext().getId();
        tripService.deleteById(id, userId);
        return ResponseEntity.ok("deleted successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(@RequestParam(name = "company_id", required = false)String id, @RequestParam(name = "status_id", required = false)Long statusId, @RequestParam(name = "truck_number", required = false, defaultValue = "")String truckNumber, @PageableDefault( sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){

        TripDto tripDto = new TripDto();
        id = StringUtils.trimToNull(id);


        List<Long> visibleIds = utilService.getVisibleIds(userService.getCurrentUserFromContext());
        tripDto.setVisibleIds(visibleIds);

        truckNumber = StringUtils.trimToNull(truckNumber);
        tripDto.setTruckNumber(truckNumber);
        tripDto.setLoadNumber(truckNumber);

        Long id_;
        try{
            id_ = Long.parseLong(truckNumber);
        }catch (Exception exception){
            id_ = null;
        }

        tripDto.setId(id_);

        if(id != null)
            tripDto.setOwnedCompanyId(Long.parseLong(id));

        if(statusId != null)
            tripDto.setStatusId(statusId);

        Page<Trip> trips = tripService.findFiltered2(tripDto, pageable);

        Map<String, Object> map = new HashMap<>();

        map.put("page", trips.getNumber());
        map.put("size", trips.getSize());
        map.put("total_pages", trips.getTotalPages());
        map.put("total_elements", trips.getTotalElements());
        map.put("number_of_elements", trips.getNumberOfElements());

        List<TripDto> tripDtoS = new ArrayList<>(trips.getSize());

        Map<Long, String> tripStatusNames = new HashMap<>();
        Map<Long, String> tripStatusColors = new HashMap<>();

        List<TripStatus> tripStatuses = tripStatusService.getAll();

        for(TripStatus tripStatus: tripStatuses){
            tripStatusNames.put(tripStatus.getId(), tripStatus.getName());
            tripStatusColors.put(tripStatus.getId(), tripStatus.getColor());
        }

        for(Trip trip: trips){
            try {
                TripDto tripDto_ = mapper.map(trip, TripDto.class);
                Driver driver = driverService.findById(trip.getDriverId());

                tripDto_.setDriverName(driverService.resolveName(driver));
                tripDto_.setDriverId(driver.getId());
                if(trip.getSecondDriverId() != null){
                    Driver teammate = driverService.findById(trip.getSecondDriverId());
                    tripDto_.setTeammateName(driverService.resolveName(teammate));
                    tripDto_.setTeammateId(teammate.getId());
                }


                List<Long> ids = trip.getLoadIds();
                Long last = ids.get(ids.size() - 1);
                Load last_ = loadService.findById(last);

                String pattern = "MM-dd-yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                if (loadService.findById(last).getDeliveries().size() >= 1) {
                    Long deliveryId = last_.getDeliveries().get(last_.getDeliveries().size() - 1);
                    Long pickupId = null;
                    pickupId = last_.getPickups().get(0);

                    Delivery delivery = deliveryService.getById(deliveryId);
                    Pickup pickup = null;
                    if (pickupId != null)
                        pickup = pickupService.getById(pickupId);

                    Company consignee = companyService.getById(delivery.getConsigneeCompanyId());

                    if(consignee.getLocationId() != null){
                        Location location = locationService.findById(consignee.getLocationId());
                        if(location != null)
                            tripDto_.setTo(packagingService.getNameAndParentAnsi(location));
                    }

                    tripDto_.setLoadId(trip.getActiveLoadId());

                    if(last_.getRcPrice() != null)
                        tripDto_.setRcPrice(String.format("%.2f", last_.getRcPrice()));
                    else tripDto_.setRcPrice("0.00");
                    if(last_.getRevisedRcPrice() != null){
                        tripDto_.setRevisedRcPrice(String.format("%.2f", last_.getRevisedRcPrice()));
                    }else tripDto_.setRevisedRcPrice("0.00");
                    assert pickup != null;
                    if(pickup.getShipperCompanyId() != null) {
                        Company shipper = companyService.getById(pickup.getShipperCompanyId());
                        if(shipper.getLocationId() != null){
                            Location location = locationService.findById(shipper.getLocationId());
                            if(location != null)
                                tripDto_.setFrom(packagingService.getNameAndParentAnsi(location));
                        }
                    }
                    tripDto_.setLoadNumber(last_.getCustomLoadNumber());

                    tripDto_.setDeliveryDate(delivery.getDeliveryDate());
                    tripDto_.setDeliveryDateFormatted(simpleDateFormat.format(delivery.getDeliveryDate()));
                    if (pickup != null) {
                        tripDto_.setPickupDate(pickup.getPickupDate());
                        tripDto_.setPickDateFormatted(simpleDateFormat.format(pickup.getPickupDate()));
                    }
                    tripDto_.setStatus("covered");
                    if(trip.getTripStatusId() != null){
                        tripDto_.setStatusName(tripStatusNames.get(trip.getTripStatusId()));
                        tripDto_.setStatusColor(tripStatusColors.get(trip.getTripStatusId()));
                    }
                }
                tripDtoS.add(tripDto_);
            }catch (NotFoundException ex){
                continue;
            }

        }

        map.put("content", tripDtoS);
        map.put("trip_statuses", tripStatusService.getAll());
        return ResponseEntity.ok(map);
    }

}
