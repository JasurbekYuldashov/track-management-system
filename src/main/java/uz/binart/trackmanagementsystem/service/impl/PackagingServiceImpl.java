package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.helpers.Util;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.*;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.service.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PackagingServiceImpl implements PackagingService {

    private final ModelMapper mapper = new ModelMapper();
    private final DriverService driverService;
    private final LoadService loadService;
    private final DeliveryService deliveryService;
    private final PickupService pickupService;
    private final CompanyService companyService;
    private final StateProvinceService stateProvinceService;
    private final UnitService unitService;
    private final LocationService locationService;
    private final TripService tripService;
    private final UtilService utilService;
    private final OwnedCompanyService ownedCompanyService;


    public TripDto packTripToDto(Trip trip){

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

        List<Long> loads = new ArrayList<>(ids);
        Long last = null;
        if (loads != null)
            last = loads.get(loads.size() - 1);

        Load last_ = loadService.findById(last);
        String pattern = "MM/dd/YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if (loadService.findById(last).getDeliveries().size() >= 1) {
            Long deliveryId = last_.getDeliveries().get(last_.getDeliveries().size() - 1);
            Long pickupId = null;
            pickupId = last_.getPickups().get(last_.getPickups().size() - 1);

            Delivery delivery = deliveryService.getById(deliveryId);
            Pickup pickup = null;
            if (pickupId != null)
                pickup = pickupService.getById(pickupId);

            Company consignee = companyService.getById(delivery.getConsigneeCompanyId());
            if(consignee.getLocationId() != null){
                Location location = locationService.findById(consignee.getLocationId());
                if(location != null)
                    tripDto_.setTo(getNameAndParentAnsi(location));
            }
            //tripDto_.setTo(consignee.getCompanyName() + ", " + consignee.getCity() + ", " + stateProvinceService.getById(consignee.getStateProvinceId()).getName());

            tripDto_.setDeliveryDate(delivery.getDeliveryDate());
            tripDto_.setDeliveryDateFormatted(simpleDateFormat.format(delivery.getDeliveryDate()));
            if (pickup != null) {
                tripDto_.setPickupDate(pickup.getPickupDate());
                tripDto_.setPickDateFormatted(simpleDateFormat.format(pickup.getPickupDate()));
            }
            tripDto_.setStatus("covered");
        }

        return tripDto_;
    }

    public LoadDto packLoadToDto(Load load){
        String pattern = "mm-dd-YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        LoadDto loadDto = mapper.map(load, LoadDto.class);
        Company customer = companyService.getById(load.getCustomerId());

        if(customer.getLocationId() != null){
            Location location = locationService.findById(customer.getLocationId());
            if(location != null)
                loadDto.setCustomer(customer.getCompanyName() + ", " + getNameAndParentAnsi(location));
        }

        if(load.getDriverId() != null) {

            loadDto.setDriverId(load.getDriverId());

            Driver driver = driverService.findById(load.getDriverId());

            if(driver.getFirstName() != null){
                loadDto.setDriverName(driver.getFirstName() + ", " + driver.getLastName());
            }else loadDto.setDriverName(driver.getLastName());
        }

        if(load.getTruckId() != null){
            Unit unit = unitService.getById(load.getTruckId());
            loadDto.setTruckNumber(unit.getNumber());
        }

        List<Long> pickups = load.getPickups();
        String nameString = "";
        if(load.getTripId() != null) {
            Optional<Trip> tripOpt = tripService.getById(load.getId());
            if(tripOpt.isPresent()) {
                Trip trip = tripOpt.get();
                loadDto.setTruckNumber(trip.getTruckId().toString());
                Driver driver = driverService.findById(load.getDriverId());


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
                if(location != null)
                    loadDto.setFrom(shipper.getCompanyName() + ", " + getNameAndParentAnsi(location));
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
                if(location != null)
                    loadDto.setTo(consignee.getCompanyName() + ", " + getNameAndParentAnsi(location));
            }

            //loadDto.setTo(consignee.getCompanyName() + ", " + consignee.getCity() + ", " + stateProvinceService.getById(consignee.getStateProvinceId()).getName());
        }
        return loadDto;
    }

    public LoadDto loadToDtoSingle(Load load){

        LoadDto loadDto = mapper.map(load, LoadDto.class);

        loadDto.setPickupsInitialized(new ArrayList<>());

        Company customer = companyService.getById(load.getCustomerId());

        if(customer.getLocationId() != null){
            Location location = locationService.findById(customer.getLocationId());

            if(location != null){
                CitySearchResultDto cityDto = packCityToDto(location);
                loadDto.setCustomer(customer.getCompanyName() + ", " + cityDto.getNameWithParentAnsi());
            }
        }

        String pattern = "MM-dd-YYYY HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if(load.getOwnedCompanyId() != null) {
            Optional<OwnedCompany> ownedCompanyOpt = ownedCompanyService.findById(load.getOwnedCompanyId());
            if (ownedCompanyOpt.isPresent()) {
                OwnedCompany ownedCompany = ownedCompanyOpt.get();
                String abbreviation = ownedCompany.getAbbreviation();
                String customLoadNumber = load.getCustomLoadNumber();
                loadDto.setCustomLoadNumber_(customLoadNumber.substring(0, customLoadNumber.indexOf(abbreviation) - 1));
            }
        }

        for(Long id_: load.getPickups()){
            Pickup pickup = pickupService.getById(id_);

            if(pickup != null) {
                Company shipper = companyService.getById(pickup.getShipperCompanyId());
                PickupDto pickupDto = mapper.map(pickup, PickupDto.class);
                pickupDto.setPickupDateFormatted(simpleDateFormat.format(pickupDto.getPickupDate()));
                pickupDto.setEpochTime(pickup.getPickupDate().getTime());
                if(shipper.getLocationId() != null) {
                    pickupDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ", " + utilService.resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                }
                loadDto.getPickupsInitialized().add(pickupDto);
            }
        }
        loadDto.setDeliveriesInitialized(new ArrayList<>());
        for(Long id_: load.getDeliveries()){
            Delivery delivery = deliveryService.getById(id_);

            if(delivery != null){
                Company shipper = companyService.getById(delivery.getConsigneeCompanyId());
                DeliveryDto deliveryDto = mapper.map(delivery, DeliveryDto.class);
                deliveryDto.setEpochTime(delivery.getDeliveryDate().getTime());
                if(shipper.getLocationId() != null)
                        deliveryDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ", " + utilService.resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                deliveryDto.setDeliveryDateFormatted(simpleDateFormat.format(delivery.getDeliveryDate()));
                loadDto.getDeliveriesInitialized().add(deliveryDto);
            }
        }

        loadDto.setOwnedCompanyName(ownedCompanyService.getNameById(loadDto.getOwnedCompanyId()));

        return loadDto;
    }

    public CitySearchResultDto packCityToDto(Location location){
        return new CitySearchResultDto(location.getId(), location.getName() + ", " + location.getParentAnsi());
    }

    public  String getNameAndParentAnsi(Location location){
        return location.getName() + ", " + location.getParentAnsi();
    }

}
