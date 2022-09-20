package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.CitySearchResultDto;
import uz.binart.trackmanagementsystem.dto.LoadDto;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.QuantityTypeService;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/load")
public class LoadController {

    private final LoadService loadService;
    private final QuantityTypeService quantityTypeService;
    private final UserService userService;
    private final UnitService unitService;
    private final CompanyService companyService;
    private final PickupService pickupService;
    private final DeliveryService deliveryService;
    private final StateProvinceService stateProvinceService;
    private final PackagingService packagingService;
    private final DriverService driverService;
    private final TripService tripService;
    private final LocationService locationService;
    private final OwnedCompanyService ownedCompanyService;
    private final UtilService utilService;
    private final ModelMapper mapper = new ModelMapper();

    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getContext(){
        Map<String, Object> result = new HashMap<>();

        result.put("quantity_types", quantityTypeService.getAll());

        result.put("owned_companies", ownedCompanyService.findAllForContext(utilService.getVisibleIds(userService.getCurrentUserFromContext())));

        return ResponseEntity.ok(result);
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> newLoad(@RequestBody LoadDto loadDto){

        Map<String, Object> answer = new HashMap<>();
        try {

            Load load = mapper.map(loadDto, Load.class);

            Long userId = userService.getCurrentUserFromContext().getId();
            Load newLoad = loadService.save(load, userId);

            if (newLoad == null) {
                answer.put("error_message", "couldn't save new entity");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(answer);
            }

            answer.put("new_load_id", load.getId());
            answer.put("error_message", "");

            return ResponseEntity.ok(answer);
        }catch(Exception exception) {
            exception.printStackTrace();
            answer.put("error_message", exception.getMessage());
            return ResponseEntity.badRequest().body(answer);
        }

    }

    @PutMapping("/edit")
    public ResponseEntity<Map<String, Object>> updateLoad(@RequestBody /*@Valid */LoadDto loadDto){

        System.out.println(loadDto.getPickups().get(0));
        Load load = mapper.map(loadDto, Load.class);
        Map<String, Object> result = new HashMap<>();
        User user = userService.getCurrentUserFromContext();

        try{
            loadService.update(load, user.getId());
            result.put(load.getId().toString(), "updated");
            result.put("error_message", "");
            return ResponseEntity.ok(result);
        }catch (Exception exception){
            exception.printStackTrace();
            result.put("error_message", exception.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
//        return ResponseEntity.ok(null);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(@RequestParam(name = "tripId", required = false)Long tripId, @RequestParam(name = "number", defaultValue = "")String number, Pageable pageable){

        List<Long> visibleIds = utilService.getVisibleIds(userService.getCurrentUserFromContext());

        Map<String, Object> map = new HashMap<>();
        number = StringUtils.trimToNull(number);
        Page<Load> loads = loadService.findFiltered(tripId, number, visibleIds, pageable);
        map.put("page", loads.getNumber());
        map.put("total_pages", loads.getTotalPages());
        map.put("total_elements", loads.getTotalElements());
        List<LoadDto> loadDtoS = new ArrayList<>(loads.getSize());

        String pattern = "MM-dd-YYYY HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        for(Load load: loads){

            LoadDto loadDto = mapper.map(load, LoadDto.class);
            Company customer = companyService.getById(load.getCustomerId());

            if(customer.getLocationId() != null){
                Location location = locationService.findById(customer.getLocationId());

                if(location != null){
                    CitySearchResultDto cityDto = packagingService.packCityToDto(location);
                    loadDto.setCustomer(customer.getCompanyName() + ", " + cityDto.getNameWithParentAnsi());
                }

            }

            if(load.getDriverId() != null) {

                loadDto.setDriverId(load.getDriverId());

                Driver driver = driverService.findById(load.getDriverId());

                if(driver.getFirstName() != null){
                    loadDto.setDriverName(driver.getFirstName() + ", " + driver.getLastName());
                }else loadDto.setDriverName(driver.getLastName());
            }

            if(load.getTruckId() != null){
                Optional<Unit> unit = unitService.getIfExists(load.getTruckId());
                unit.ifPresent(value -> loadDto.setTruckNumber(value.getNumber()));
            }

            List<Long> pickups = load.getPickups();
            String nameString = "";
            if(load.getTripId() != null) {
                Optional<Trip> tripOpt = tripService.getById(load.getTripId());
                if(tripOpt.isPresent()) {
                    Trip trip = tripOpt.get();
                    loadDto.setTruckNumber(trip.getTruckId().toString());
                    if(load.getDriverId() != null) {
                        Driver driver = driverService.findById(load.getDriverId());

                        nameString = driverService.resolveName(driver);
                    }
                }
                loadDto.setDriverName(nameString);
            }
            if(pickups.size() >= 1){
                Pickup pickup = pickupService.getById(pickups.get(0));
                Company shipper = companyService.getById(pickup.getShipperCompanyId());
                loadDto.setPickupDate(pickup.getPickupDate());
                loadDto.setPickupDateFormatted(simpleDateFormat.format(loadDto.getPickupDate()));

                if(shipper.getLocationId() != null){
                    Location location = locationService.findById(shipper.getLocationId());
                    if(location != null){
                        CitySearchResultDto dto_ = packagingService.packCityToDto(location);
                        loadDto.setFrom(shipper.getCompanyName() + ", " + dto_.getNameWithParentAnsi());
                    }

                }

            }
            List<Long> deliveries = load.getDeliveries();

            if(deliveries.size() >= 1){
                Delivery delivery = deliveryService.getById(deliveries.get(deliveries.size() - 1));
                Company consignee = companyService.getById(delivery.getConsigneeCompanyId());
                loadDto.setDeliveryDate(delivery.getDeliveryDate());
                loadDto.setDeliveryDateFormatted(simpleDateFormat.format(loadDto.getDeliveryDate()));

                if(consignee.getLocationId() != null){
                    Location location = locationService.findById(consignee.getLocationId());
                    if(location != null){
                        CitySearchResultDto dto_ = packagingService.packCityToDto(location);
                        loadDto.setTo(consignee.getCompanyName() + ", " + dto_.getNameWithParentAnsi());
                    }

                }
            }
            loadDtoS.add(loadDto);
        }

        map.put("content", loadDtoS);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoadDto> findById(@PathVariable Long id){

        Load load = loadService.findById(id);

        long time = System.currentTimeMillis();
        LoadDto loadDto = packagingService.loadToDtoSingle(load);

        boolean canBeChanged = (load.getPickupDeliveryCanBeUpdatedUntil() == null ||  load.getPickupDeliveryCanBeUpdatedUntil() != null && time < load.getPickupDeliveryCanBeUpdatedUntil());

       /* boolean canBeChanged = load.getPickupDeliveryCanBeUpdatedUntil() != null;*/

        loadDto.setCanBeChanged(canBeChanged);

        return ResponseEntity.ok(loadDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id){
        Long userId = userService.getCurrentUserFromContext().getId();
        loadService.deleteById(id, userId);
        return ResponseEntity.ok("deleted successfully");
    }


}
