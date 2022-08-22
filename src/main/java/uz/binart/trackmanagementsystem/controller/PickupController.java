package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.CitySearchResultDto;
import uz.binart.trackmanagementsystem.dto.PickupDto;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.model.Pickup;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pickup")
public class PickupController {

    private final PickupService pickupService;
    private final UserService userService;
    private final CompanyService companyService;
    private final PackagingService packagingService;
    private final LocationService locationService;
    private final ModelMapper mapper = new ModelMapper();

    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid PickupDto pickupDto) throws ParseException {
        Pickup pickup = mapper.map(pickupDto, Pickup.class);
        pickup.setPickupDate(new Date(pickupDto.getPickupDate_()));
        Long userId = userService.getCurrentUserFromContext().getId();
        Pickup newPickup = pickupService.save(pickup, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("id", newPickup.getId());
        map.put("has_attachment",newPickup.getBolId() != null);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        Pickup pickup = pickupService.getById(id);

        PickupDto pickupDto = mapper.map(pickup, PickupDto.class);

        Company customer = companyService.getById(pickup.getShipperCompanyId());

        if(customer.getLocationId() != null){
            Location location = locationService.findById(customer.getLocationId());

            if(location != null){
                CitySearchResultDto cityDto = packagingService.packCityToDto(location);
                pickupDto.setShipperCompany(customer.getCompanyName() + ", " + cityDto.getNameWithParentAnsi());
            }
        }

        String pattern = "MM-dd-YYYY HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        pickupDto.setPickupDateFormatted(simpleDateFormat.format(pickup.getPickupDate()));
        pickupDto.setPickupDate_(pickup.getPickupDate().getTime());
        return ResponseEntity.ok(pickupDto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Pickup>> getList(Pageable pageable){
        return ResponseEntity.ok(pickupService.findFiltered(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id){
        Long userId = userService.getCurrentUserFromContext().getId();
        pickupService.deleteById(id, userId);
        return ResponseEntity.ok("deleted successfully entity with " + id + " id");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> update(@RequestBody @Valid PickupDto pickupDto) throws ParseException {
        pickupDto.setPickupDate(new Date(pickupDto.getPickupDate_()));
        Pickup pickup = mapper.map(pickupDto, Pickup.class);
        pickup.setPickupDate(new Date(pickupDto.getPickupDate_()));
        User user = userService.getCurrentUserFromContext();
        pickupService.update(pickup, user.getId());
        return ResponseEntity.ok("updated successfully");
    }

    @GetMapping("/resolved_date/{timeStamp}")
    public ResponseEntity<String> getDate(@PathVariable @NotNull Long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
        return ResponseEntity.ok(simpleDateFormat.format(date));
    }

}
