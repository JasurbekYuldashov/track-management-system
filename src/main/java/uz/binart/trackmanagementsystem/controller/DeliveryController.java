package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.CitySearchResultDto;
import uz.binart.trackmanagementsystem.dto.DeliveryDto;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.model.Delivery;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UserService userService;
    private final CompanyService companyService;
    private final PackagingService packagingService;
    private final LocationService locationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/new")
    public ResponseEntity<Long> createNewDelivery(@RequestBody @Valid DeliveryDto deliveryDto) throws ParseException {
        Delivery delivery = modelMapper.map(deliveryDto, Delivery.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        delivery.setDeliveryDate(new Date(deliveryDto.getDeliveryDate_()));
        Long userId = userService.getCurrentUserFromContext().getId();
        Delivery newDelivery = deliveryService.save(delivery, userId);

        return ResponseEntity.ok(newDelivery.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDto> getById(@PathVariable @NotNull Long id){
        DeliveryDto deliveryDto = modelMapper.map(deliveryService.getById(id), DeliveryDto.class);

        Company customer = companyService.getById(deliveryDto.getConsigneeCompanyId());

        if(customer.getLocationId() != null){
            Location location = locationService.findById(customer.getLocationId());

            if(location != null){
                CitySearchResultDto cityDto = packagingService.packCityToDto(location);
                deliveryDto.setConsigneeCompany(customer.getCompanyName() + ", " + cityDto.getNameWithParentAnsi());
            }
        }


        String pattern = "MM-dd-YYYY HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        deliveryDto.setDeliveryDateFormatted(simpleDateFormat.format(deliveryDto.getDeliveryDate()));
        deliveryDto.setDeliveryDate_(deliveryDto.getDeliveryDate().getTime());
        return ResponseEntity.ok(deliveryDto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Delivery>> getList(Pageable pageable){
        return ResponseEntity.ok(deliveryService.findFiltered(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @NotNull Long id){
        Long userId = userService.getCurrentUserFromContext().getId();
        deliveryService.deleteById(id, userId);
        return ResponseEntity.ok("deleted successfully");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> edit(@RequestBody @Valid DeliveryDto deliveryDto) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
      //  deliveryDto.setDeliveryDateFormatted(simpleDateFormat.parse(new Date(deliveryDto.getDeliveryDate_())).toString());
        Delivery delivery = modelMapper.map(deliveryDto, Delivery.class);
        delivery.setDeliveryDate(new Date(deliveryDto.getDeliveryDate_()));
        User user = userService.getCurrentUserFromContext();
        deliveryService.update(delivery, user.getId());
        return ResponseEntity.ok("updated successfully");
    }
}
