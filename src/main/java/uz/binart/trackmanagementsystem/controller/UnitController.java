package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.ChangeStatusDto;
import uz.binart.trackmanagementsystem.dto.UnitCompactDto;
import uz.binart.trackmanagementsystem.dto.UnitDto;
import uz.binart.trackmanagementsystem.model.OwnedCompany;
import uz.binart.trackmanagementsystem.model.StateProvince;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;
import uz.binart.trackmanagementsystem.model.type.DriverType;
import uz.binart.trackmanagementsystem.model.Unit;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.repository.status.UnitStatusRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.DriverTypeService;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

import static uz.binart.trackmanagementsystem.dto.ResponseData.response;
import static uz.binart.trackmanagementsystem.dto.ResponseData.responseBadRequest;

@RestController
@RequestMapping("/unit")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;
    private final UnitTypeService unitTypeService;
    private final UserService userService;
    private final DriverTypeService driverTypeService;
    private final UnitStatusRepository unitStatusService;
    private final FileService fileService;
    private final TeamService teamService;
    private final OwnedCompanyService ownedCompanyService;
    private final StateProvinceService stateProvinceService;
    private final DriverService driverService;
    private final UtilService utilService;
    private final TruckNoteService truckNoteService;
    private final ModelMapper mapper = new ModelMapper( );

    @GetMapping("/context")
    public ResponseEntity<?> getContext(){
        Map<String, Object> res = new HashMap<>();

        res.put("unit_types", unitTypeService.getAll());
        res.put("ownership_types", driverTypeService.getAll());
        res.put("unit_statuses", unitStatusService.findAll());
        User user = userService.getCurrentUserFromContext();
        List<Long> visibleTeamIds = utilService.getVisibleTeamIds(user);
        res.put("teams", teamService.findVisible(visibleTeamIds));
        res.put("location", stateProvinceService.getAll());

        return ResponseEntity.ok(res);
    }

    @GetMapping("/context/lbs")
    public ResponseEntity<?> getContextLbs(){
        Map<String, Object> res = new HashMap<>();

        res.put("unit_types", unitTypeService.getAll());
        res.put("ownership_types", driverTypeService.getAll());
        res.put("unit_statuses", unitStatusService.findAll());
        res.put("teams", teamService.findAll());
        res.put("location", stateProvinceService.getAll());
        res.put("drivers", driverService.findAllForUnitDto());

        return ResponseEntity.ok(res);
    }


    @PostMapping("/new")
    public ResponseEntity<String> newUnit(@RequestBody @Valid UnitDto unitDto){

        if(unitDto.getNumber() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("number should be specified");
        if(unitDto.getUnitTypeId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("type should be specified");
        if(unitDto.getOwnershipTypeId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ownership type should be specified");

        Unit unit = mapper.map(unitDto, Unit.class);

        if(setEmployerId(unit)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("unit number should be specified as sequence of characters(Y) and company abbreviation(X), for example YYYYYY-XXXX");
        }

        Long userId = userService.getCurrentUserFromContext().getId();
        unitService.create(unit, userId);

        return ResponseEntity.ok("new unit was created");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUnit(@RequestBody @Valid UnitDto unitDto){

        if(unitDto.getNumber() == null)
            return responseBadRequest("number should be specified");
        if(unitDto.getUnitTypeId() == null)
            return responseBadRequest("type should be specified");
        if(unitDto.getOwnershipTypeId() == null)
            return responseBadRequest("ownership type should be specified");

        Unit unit = mapper.map(unitDto, Unit.class);

        if(setEmployerId(unit)){
            return responseBadRequest("unit number should be specified as sequence of characters(Y) and company abbreviation(X), for example YYYYYY-XXXX");
        }

        Long userId = userService.getCurrentUserFromContext().getId();
        unitService.create(unit, userId);

        return response("new unit was created");
    }


    @PutMapping("/edit")
    public ResponseEntity<String> updateUnit(@RequestBody @Valid UnitDto unitDto){
        Unit unit = unitService.getById(unitDto.getId());
        String oldNotes = StringUtils.trimToEmpty(unit.getNotes());
        if(!unit.getNumber().equals(unitDto.getNumber())){
            if(unitService.existsByNumber(unitDto.getNumber())){
                return  ResponseEntity.status(HttpStatus.CONFLICT).body("unit with such number already exists");
            }
        }

        Map<Long, String> oldFiles = unit.getFiles();
        Map<Long, String> newFiles = unitDto.getFiles();

        User user = userService.getCurrentUserFromContext();

        if(oldFiles != null && newFiles != null) {
            Set<Long> oldKeys = oldFiles.keySet();
            Set<Long> newKeys = newFiles.keySet();
            fileService.updateFiles(newKeys, oldKeys, user.getId());
        }

        boolean licensePlateWasUpdated = false, annualInspectionTimeWasUpdated = false, registrationExpirationTimeWasUpdated = false;
        if(unitDto.getLicensePlateExpirationTime() != null && unit.getLicensePlateExpirationTime() != null && !unit.getLicensePlateExpirationTime().equals(unitDto.getLicensePlateExpirationTime()) ||
                unit.getLicensePlateExpirationTime() == null && unitDto.getLicensePlateExpirationTime() != null
        ){
            licensePlateWasUpdated = true;
        }

        if(unitDto.getAnnualInspectionExpirationTime() != null && unit.getAnnualInspectionExpirationTime() != null && !unit.getAnnualInspectionExpirationTime().equals(unitDto.getAnnualInspectionExpirationTime()) ||
                unit.getAnnualInspectionExpirationTime() == null && unitDto.getAnnualInspectionExpirationTime() != null
        ){
            annualInspectionTimeWasUpdated = true;
        }


        if(unitDto.getRegistrationExpirationTime() != null && unit.getRegistrationExpirationTime() != null && !unit.getRegistrationExpirationTime().equals(unitDto.getRegistrationExpirationTime()) ||
                unit.getRegistrationExpirationTime() == null && unitDto.getRegistrationExpirationTime()!= null
        ){
            registrationExpirationTimeWasUpdated = true;
        }

        unit = mapper.map(unitDto, Unit.class);

        unit.setNotifiedOfLicensePlateExpiration(!licensePlateWasUpdated);
        unit.setNotifiedOfInspection(!annualInspectionTimeWasUpdated);
        unit.setNotifiedOfRegistration(!registrationExpirationTimeWasUpdated);

        if(setEmployerId(unit)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("unit number should be specified as sequence of characters(Y) and company abbreviation(X), for example YYYYYY-XXXX");
        }

        unit.setDeleted(false);
        if(unit.getIsActive() == null) unit.setIsActive(true);
        Unit updatedUnit = unitService.update(unit);

        String updatedNotes = StringUtils.trimToEmpty(updatedUnit.getNotes());

        if(!oldNotes.equals(updatedNotes)){
            truckNoteService.save(updatedNotes, updatedUnit.getId(), user.getId());
        }

        if(updatedUnit != null)
            return ResponseEntity.ok("unit was updated");
        else
            return ResponseEntity.badRequest().body("unable to update");
    }

    @GetMapping("/{number}")
    public ResponseEntity<UnitDto> getByNumber(@PathVariable @NotNull String number){

        if(!unitService.existsByNumber(number))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        Unit unit = unitService.getByNumber(number);
        ModelMapper mapper = new ModelMapper();
        UnitDto unitDto = mapper.map(unit, UnitDto.class);
        DriverType driverType = driverTypeService.getById(unit.getOwnershipTypeId());
        unitDto.setOwnershipName(driverType.getName());
        unitDto.setUnitName(unitTypeService.getById(unit.getUnitTypeId()).getName());
        unitDto.setUnitTypeName(driverTypeService.getById(unit.getOwnershipTypeId()).getAbbreviation());

        if(unit.getInitialLocation().containsKey("stateProvince")){
            Long stateProvinceId = Long.parseLong(unit.getInitialLocation().get("stateProvince"));
            StateProvince stateProvince = stateProvinceService.getById(stateProvinceId);
            if(stateProvince != null){
                unitDto.setStateProvinceName(stateProvince.getName());
            }else
                unitDto.setStateProvinceName("");
        }

        if(unit.getUnitStatusId() != null)
            unitDto.setStatus(unitStatusService.getOne(unit.getUnitStatusId()).getName());

        String pattern = "YYYY-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if(unit.getLicensePlateExpiration() != null)
            unitDto.setLicenseExpirationFormatted(simpleDateFormat.format(unit.getLicensePlateExpiration()));
        if(unit.getInspectionStickerExpiration() != null)
            unitDto.setInspectionStickerExpirationFormatted(simpleDateFormat.format(unit.getInspectionStickerExpiration()));

        return ResponseEntity.ok(unitDto);
    }

    @DeleteMapping("/delete_by_number/{number}")
    public ResponseEntity<String> deleteByNumber(@PathVariable @NotNull String number){
        if(!unitService.existsByNumber(number))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no unit with such number");
        Long userId = userService.getCurrentUserFromContext().getId();
        unitService.deleteByNumber(number, userId);
        return ResponseEntity.ok("unit successfully deleted");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable @NotNull Long id){
        if(!unitService.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no unit with such id");
        unitService.deleteById(id);
        return ResponseEntity.ok("unit successfully deleted");
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(UnitDto unitDto,
                                                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){

        User user = userService.getCurrentUserFromContext();
        List<Long> visibleIds = utilService.getVisibleIds(user);
        List<Long> visibleTeamIds = utilService.getVisibleTeamIds(user);
        unitDto.setVisibleIds(visibleIds);
        unitDto.setVisibleTeamIds(visibleTeamIds);
        Page<Unit> units = unitService.findFiltered(unitDto, pageable);

        ModelMapper modelMapper = new ModelMapper();

        List<UnitDto> unitDtoS = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        map.put("number", units.getNumber());
        map.put("numberOfElements", units.getNumberOfElements());
        map.put("size", units.getSize());
        map.put("totalPages", units.getTotalPages());
        map.put("totalElements", units.getTotalElements());
        map.put("sort", units.getSort());
        map.put("pageable", units.getPageable());
        String pattern = "YYYY-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        List<UnitStatus> unitStatuses = unitStatusService.findAll();

        Map<Long, String> unitStatusNames = new HashMap<>();
        Map<Long, String> unitStatusColors = new HashMap<>();

        for(UnitStatus unitStatus: unitStatuses){
            unitStatusNames.put(unitStatus.getId(), unitStatus.getName());
            unitStatusColors.put(unitStatus.getId(), unitStatus.getColor());
        }

        for(Unit unit: units){
            UnitDto unitDto_ = modelMapper.map(unit, UnitDto.class);
            DriverType driverType = driverTypeService.getById(unit.getOwnershipTypeId());
            unitDto_.setOwnershipName(driverType.getName());
            unitDto_.setUnitName(unitTypeService.getById(unit.getUnitTypeId()).getName());
            unitDto_.setUnitTypeName(driverType.getAbbreviation());

            if(unit.getUnitStatusId() != null) {
                unitDto_.setStatusName(unitStatusNames.get(unit.getUnitStatusId()));
                unitDto_.setUnitStatusColor(unitStatusColors.get(unit.getUnitStatusId()));
            }
            if(unit.getLicensePlateExpiration() != null)
                unitDto_.setLicenseExpirationFormatted(simpleDateFormat.format(unit.getLicensePlateExpiration()));
            else unitDto_.setLicenseExpirationFormatted("");
            if(unit.getInspectionStickerExpiration() != null)
                unitDto_.setInspectionStickerExpirationFormatted(simpleDateFormat.format(unit.getInspectionStickerExpiration()));
            else unitDto_.setInspectionStickerExpirationFormatted("");

            unitDtoS.add(unitDto_);
        }

        map.put("content", unitDtoS);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/search_by_number")
    public ResponseEntity<?> searchByNumber(@RequestParam(name = "q", required = false, defaultValue = "")String number){

        List<Long> visibleIds = utilService.getVisibleIds(userService.getCurrentUserFromContext());

        return ResponseEntity.ok(packToCompact(unitService.findByNumber(number, visibleIds)));
    }


    @PutMapping("/update_status/{id}/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable @NotNull Long id, @PathVariable @NotNull Long statusId, @RequestBody ChangeStatusDto dto){
        User user = userService.getCurrentUserFromContext();

        Long eldUntil = dto.getEldUnTil();

        unitService.changeStatus(id, statusId, user.getId(), eldUntil);

        return ResponseEntity.ok("Unit status updated successfully");
    }

    private List<UnitCompactDto> packToCompact(List<Unit> units){
        List<UnitCompactDto> dtoS = new ArrayList<>(units.size());
        for(Unit unit: units){
            dtoS.add(mapper.map(unit, UnitCompactDto.class));
        }
        return dtoS;
    }

    @GetMapping("/loading")
    public ResponseEntity<?> getUnitLoads(Long start, Long end){
        User user = userService.getCurrentUserFromContext();
        List<Long> visibleIds = utilService.getVisibleIds(user);
        List<Long> visibleTeamIds = utilService.getVisibleTeamIds(user);
        return response(unitService.getUnitsLoad(start, end, visibleIds, visibleTeamIds));
    }

    private boolean setEmployerId(Unit unit){

        String[] unitNumber = unit.getNumber().split("-");

        if(unitNumber.length <= 1){
            return true;
        }

        String companyAbbreviation = unitNumber[1];

        Optional<OwnedCompany> ownedCompanyOptional = ownedCompanyService.findByAbbreviationOptional(companyAbbreviation);

        ownedCompanyOptional.ifPresent(ownedCompany -> unit.setEmployerId(ownedCompany.getId()));

        return false;
    }


}
