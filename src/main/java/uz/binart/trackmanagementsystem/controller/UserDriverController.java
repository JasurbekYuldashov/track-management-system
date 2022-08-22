package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.binart.trackmanagementsystem.dto.*;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.model.type.DriverType;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.DriverStatusService;
import uz.binart.trackmanagementsystem.service.type.DriverTypeService;
import uz.binart.trackmanagementsystem.service.type.PaymentTypeService;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-driver")
public class UserDriverController {

    private final DriverService driverService;
    private final UserService userService;
    private final UnitService unitService;
    private final DriverStatusService driverStatusService;
    private final PaymentTypeService paymentTypeService;
    private final TripService tripService;
    private final PickupService pickupService;
    private final DeliveryService deliveryService;
    private final DriverTypeService driverTypeService;
    private final UtilService utilService;
    private final FileService fileService;
    private final DriverSupportRequestService driverSupportRequestService;
    private final LoadService loadService;
    private final LocationService locationService;
    private final CompanyService companyService;

    @GetMapping("/cache")
    public ResponseEntity<?> getCache(){

        Map<String, Object> map = new HashMap<>();
        map.put("payment_types", paymentTypeService.getAll());
        map.put("driver_statuses", driverStatusService.getAll());

        MobileDto mobileDto = new MobileDto();
        mobileDto.setData(map);

        return ResponseEntity.ok(mobileDto);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(){
        User user = userService.getCurrentUserFromContext();

        if(user == null || user.getRoleId() != 4){

            MobileDto mobileDto = new MobileDto();
            mobileDto.setErrorMessage("don't have permission");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        Map<String, Object> map = new HashMap<>();
        MobileDto mobileDto = new MobileDto();

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        Driver driver = driverService.findById(user.getAttachedDriverId());

        if(driver == null){
            mobileDto.setErrorMessage("driver not found");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        map.put("first_name", driver.getFirstName());
        map.put("last_name", driver.getLastName());
        map.put("phone_number", driver.getPhone());

        if(driver.getTruckId() != null) {
            Unit truck = unitService.getById(driver.getTruckId());
            if(truck != null){
                if(truck.getNumber() != null){
                    map.put("truck_number", truck.getNumber());
                }
            }
        }

        map.put("email", driver.getEmail());

        mobileDto.setData(map);

        return ResponseEntity.ok(mobileDto);
    }

    @PostMapping("/info")
    public ResponseEntity<?> postNewInfo(@RequestBody DriverInfoDto info){
        User user = userService.getCurrentUserFromContext();

        if(user == null || user.getRoleId() != 4){

            MobileDto mobileDto = new MobileDto();
            mobileDto.setErrorMessage("don't have permission");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        Map<String, Object> map = new HashMap<>();
        MobileDto mobileDto = new MobileDto();

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        Driver driver = driverService.findById(user.getAttachedDriverId());

        if(driver == null){
            mobileDto.setErrorMessage("driver not found");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        driver.setFirstName(info.getFirstName());
        driver.setLastName(info.getLastName());

        if(driver.getTruckId() != null) {
            Unit truck = unitService.getById(driver.getTruckId());
            if(truck != null){
                if(truck.getNumber() != null){
                    truck.setNumber(info.getTruckNumber());
                    unitService.update(truck);
                }
            }
        }
        driver.setPhone(info.getPhoneNumber());
        driver.setEmail(info.getEmail());
        driverService.update(driver);

        mobileDto.setData("driver was updated successfully");

        return ResponseEntity.ok(mobileDto);
    }


    @PutMapping("/status/{statusId}")
    public ResponseEntity<?> switchStatus(@PathVariable Long statusId){

        User user = userService.getCurrentUserFromContext();

        if(user == null || user.getRoleId() != 4){
            MobileDto mobileDto = new MobileDto();
            mobileDto.setErrorMessage("don't have permission");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        MobileDto mobileDto = new MobileDto();

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        driverService.updateStatus(user.getAttachedDriverId(), statusId, user.getId());

        return ResponseEntity.ok(mobileDto);
    }

    @GetMapping("/trips")
    public ResponseEntity<?> getTripsForDriver(@RequestParam(name = "status_id", defaultValue = "1L")Long statusId, @PageableDefault(page = 0, size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable){

        User user = userService.getCurrentUserFromContext();
        MobileDto mobileDto = new MobileDto();
        if(user == null || user.getRoleId() != 4){
            mobileDto.setErrorMessage("don't have permission");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("user has no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        TripDto tripDto = new TripDto();

        tripDto.setDriverId(user.getAttachedDriverId());
        tripDto.setStatusId(statusId);

        Page<Trip> trips = tripService.findFiltered2(tripDto, pageable);

        Map<String, Object> data = new HashMap<>();

        data.put("page_number", trips.getNumber());
        data.put("total_pages", trips.getTotalPages());
        data.put("content", collectData(trips.getContent()));

        mobileDto.setData(data);

        return ResponseEntity.ok(mobileDto);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getTripInfo(@PathVariable Long tripId){

        User user = userService.getCurrentUserFromContext();
        MobileDto mobileDto = new MobileDto();
        if(user == null || user.getRoleId() != 4){

            mobileDto.setErrorMessage("don't have permission");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("user has no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        Optional<Trip> tripOptional = tripService.getById(tripId);
        if(tripOptional.isEmpty()) {
            mobileDto.setErrorMessage("couldn't find such trip");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mobileDto);
        }

        Trip trip = tripOptional.get();

        TripDto tripDto = new TripDto();

        Pair<List<Long>, List<Long>> pickupsAndDeliveries = loadService.getAllPickupsByTripId(trip.getId());

        tripDto.setChronologicalSequence(utilService.sortByPickupOrDeliveryDate(pickupsAndDeliveries.getFirst(), pickupsAndDeliveries.getSecond(), true));

        List<Map<String, Object>> data = new ArrayList<>();

        for(int i = 0; i < tripDto.getChronologicalSequence().size(); i++){
            Object point = tripDto.getChronologicalSequence().get(i);
            try{
                PickupDto pickupDto = (PickupDto)point;
                Map<String, Object> map = convertToData(pickupDto);
                data.add(map);
            }catch(Exception exception){
                assert false;
                DeliveryDto deliveryDto = (DeliveryDto)point;
                boolean last = false;
                if(i == tripDto.getChronologicalSequence().size() - 1)
                   last = true;
                Map<String, Object> map = convertToData(deliveryDto, last);
                data.add(map);
            }
        }

        List<Map<String, Object>> stageSequence = utilService.createSequence(pickupsAndDeliveries.getFirst(), pickupsAndDeliveries.getSecond());
        if(stageSequence.size() == data.size())
        for(int i = 0; i < data.size(); i++){
            Map<String, Object> point = data.get(i);
            Map<String, Object> stage = stageSequence.get(i);
            point.put("order", stage.get("id"));
            point.put("value", stage.get("value"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("stages", data);

        mobileDto.setData(map);

        return ResponseEntity.ok(mobileDto);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam(name = "stop_id")Long stopId, @RequestParam(name = "file")MultipartFile file){

        MobileDto mobileDto = new MobileDto();
        Map<String, Long> map = new HashMap<>();
        User user = userService.getCurrentUserFromContext();

        Long savedInfoId = fileService.storeFile(file, user.getId());

        map.put("uploaded_file_id", savedInfoId);
        if(stopId != null) {

            if(pickupService.existsById(stopId)){
                pickupService.addFileToDriversUploads(savedInfoId, stopId);
            }
            else if(deliveryService.existsById(stopId)){
                deliveryService.addFileToDriversUploads(savedInfoId, stopId);
            }else{
                mobileDto.setErrorMessage("no stop with " + stopId);
                return ResponseEntity.badRequest().body(mobileDto);
            }
        }

        mobileDto.setData(map);

        return ResponseEntity.ok(mobileDto);
    }

    @GetMapping(value = "/file/{file_id}", produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<Resource> viewFile2(@PathVariable(name = "file_id") Long fileId) throws IOException {
        Optional<FileInformation> fileInformationOpt = fileService.getInfoById(fileId);

        if(fileInformationOpt.isPresent() && !fileInformationOpt.get().getDeleted()) {
            FileInformation fileInformation = fileInformationOpt.get();
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInformation.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInformation.getOriginalFileName() + "\"")
                    .body(new ByteArrayResource(
                            FileUtils.readFileToByteArray(new java.io.File(fileInformation.getFileNameWithPath()))
                    ));
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/support")
    public ResponseEntity<MobileDto> getSupport(){

        User user = userService.getCurrentUserFromContext();
        MobileDto mobileDto = new MobileDto();
        if(user == null || user.getRoleId() != 4){

            mobileDto.setErrorMessage("don't have permission");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("user has no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        Driver driver = driverService.findById(user.getAttachedDriverId());

        if(driver == null){
            mobileDto.setErrorMessage("no attached driver found");
            return ResponseEntity.badRequest().body(mobileDto);
        }

        List<String> phoneNumbers = new ArrayList<>(3);

        if(driver.getPhone() != null && !driver.getPhone().isEmpty())
            phoneNumbers.add(driver.getPhone());

        if(driver.getAlternatePhone() != null && !driver.getAlternatePhone().isEmpty())
            phoneNumbers.add(driver.getAlternatePhone());

        List<String> messages = new ArrayList<>(Arrays.asList("Trailer breakdown", "Truck breakdown", "PM service", "Road service", "Medical issue", "Weather issues"));

        Map<String, List<String>> phoneNumbersAndMessageTemplates = new HashMap<>();

        phoneNumbersAndMessageTemplates.put("phone_numbers", phoneNumbers);
        phoneNumbersAndMessageTemplates.put("messages", messages);

        mobileDto.setData(phoneNumbersAndMessageTemplates);

        return ResponseEntity.ok(mobileDto);
    }

    @PostMapping("/support_request")
    public ResponseEntity<MobileDto> postSupportRequest(@RequestBody @Valid DriverSupportRequestDto supportRequest){

        User user = userService.getCurrentUserFromContext();
        MobileDto mobileDto = new MobileDto();
        if(user == null || user.getRoleId() != 4){

            mobileDto.setErrorMessage("don't have permission");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getAttachedDriverId() == null){
            mobileDto.setErrorMessage("user has no attached driver");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(mobileDto);
        }

        Driver driver = driverService.findById(user.getAttachedDriverId());
        DriverSupportRequest driverSupportRequest = new DriverSupportRequest();
        driverSupportRequest.setDriverId(driver.getId());
        driverSupportRequest.setUserId(user.getId());
        driverSupportRequest.setPhoneNumber(supportRequest.getPhoneNumber());
        driverSupportRequest.setMessage(supportRequest.getMessage());
        driverSupportRequestService.save(driverSupportRequest);

        mobileDto.setData("support request was sent");

        return ResponseEntity.ok(mobileDto);
    }


    private TripListMobileDto packToDto(Trip trip, Driver driver, Load load){

        TripListMobileDto dto = new TripListMobileDto();

        dto.setId(trip.getId());

        if(driver.getDriverTypeId() != null) {
            DriverType type = driverTypeService.getById(driver.getDriverTypeId());
            if (type != null) {
                dto.setDriverTypeName(type.getName());
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-YYYY HH:mm");

        dto.setOdometer(trip.getOdometer());
        dto.setRcPrice(load.getRcPrice());

        Pair<PickupDto, DeliveryDto> firstPickupAndLastDelivery = utilService.firstPickupAndLastDelivery(load.getPickups(), load.getDeliveries());
        dto.setStartLocation(firstPickupAndLastDelivery.getFirst().getConsigneeNameAndLocation());
        dto.setEndLocation(firstPickupAndLastDelivery.getSecond().getConsigneeNameAndLocation());
        dto.setStartTime(firstPickupAndLastDelivery.getFirst().getPickupDateFormatted());
        dto.setEndTime(firstPickupAndLastDelivery.getSecond().getDeliveryDateFormatted());
        List<Map<String, Object>> stageSequence = utilService.createSequence(load.getPickups(), load.getDeliveries());
        dto.setStages(stageSequence);
        return dto;
    }

    @PostMapping("/pickup_drop_finish")
    public ResponseEntity<?> postMapping(@RequestBody @Valid PickupDropFinish dto){
        MobileDto mobileDto = new MobileDto();
        boolean pickup = false;
        if(pickupService.existsById(dto.getStopId())){
            pickupService.setDriversPickupTime(dto.getStopId(), dto.getTimeStamp());
            pickup = true;
        }
        else if(deliveryService.existsById(dto.getStopId())){
            deliveryService.setDriversDeliveryTime(dto.getStopId(), dto.getTimeStamp());
        }else{
            mobileDto.setErrorMessage("no stop with " + dto.getStopId());
            return ResponseEntity.badRequest().body(mobileDto);
        }

        Map<String, Object> map = new HashMap<>();
        if(pickup)
            map.put("message", "pickup time was set");
        else
            map.put("message", "delivery time was set");
        mobileDto.setData(map);
        return ResponseEntity.ok(mobileDto);
    }

    private List<TripListMobileDto> collectData(List<Trip> trips){

        List<TripListMobileDto> dtoS = new ArrayList<>();

        for(Trip trip: trips){
            Load load = tripService.getTripsLoad(trip);
            Driver driver = driverService.findById(trip.getDriverId());
            dtoS.add(packToDto(trip, driver, load));
        }

        return dtoS;
    }


    private Map<String, Object> convertToData(PickupDto pickupDto){
        String pickupPlaceNameAndLocation = pickupDto.getConsigneeNameAndLocation();
        String[] consigneeAndLocation = pickupPlaceNameAndLocation.split("\\|");

        Map<String, Object> map = new HashMap<>();
        map.put("id", pickupDto.getId());

        map.put("consignee",consigneeAndLocation[0]);
        map.put("location", consigneeAndLocation[1]);
        map.put("time_zone_abbreviation", "GMT");
        //map.put("time_zone", )
        Company company = companyService.getById(pickupDto.getShipperCompanyId());
        Integer timezone = locationService.getParentTimeZone(company.getLocationId());
        map.put("time_zone", timezone);

        Date date = pickupDto.getPickupDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
        String dateFormatted = simpleDateFormat.format(date);
        String[] dateAndTime = dateFormatted.split(" ");
        map.put("date", dateAndTime[0]);
        map.put("time", dateAndTime[1]);
        map.put("driver_uploads", pickupService.getUploadsWithIdsAndNames(pickupDto.getId()));
        map.put("action_name", "pickup");
        map.put("drivers_timestamp", pickupDto.getDriversPickupTime());
        return map;
    }

    private Map<String, Object> convertToData(DeliveryDto deliveryDto, boolean last){
        String pickupPlaceNameAndLocation = deliveryDto.getConsigneeNameAndLocation();
        String[] consigneeAndLocation = pickupPlaceNameAndLocation.split("\\|");
        Map<String, Object> map = new HashMap<>();
        map.put("id", deliveryDto.getId());
        map.put("consignee",consigneeAndLocation[0]);
        map.put("location", consigneeAndLocation[1]);
        Company company = companyService.getById(deliveryDto.getConsigneeCompanyId());
        Integer timezone = locationService.getParentTimeZone(company.getLocationId());
        map.put("time_zone_abbreviation", "GMT");
        map.put("time_zone", timezone);

        Date date = deliveryDto.getDeliveryDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
        String dateFormatted = simpleDateFormat.format(date);
        String[] dateAndTime = dateFormatted.split(" ");
        map.put("date", dateAndTime[0]);
        map.put("time", dateAndTime[1]);
        map.put("driver_uploads", deliveryService.getUploadsWithIdsAndNames(deliveryDto.getId()));
        if(!last)
            map.put("action_name", "drop");
        else map.put("action_name", "finish");
        map.put("drivers_timestamp", deliveryDto.getDriversDeliveryTime());
        return map;
    }


}
