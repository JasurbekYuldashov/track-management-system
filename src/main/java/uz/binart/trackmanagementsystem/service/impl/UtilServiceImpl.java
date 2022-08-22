package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.DeliveryDto;
import uz.binart.trackmanagementsystem.dto.PickupDto;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.repository.TeamRepository;
import uz.binart.trackmanagementsystem.service.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UtilServiceImpl implements UtilService {

    private final PickupService pickupService;
    private final DeliveryService deliveryService;
    private final CompanyService companyService;
    private final LocationService locationService;
    private final OwnedCompanyService ownedCompanyService;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public List<Object> sortByPickupOrDeliveryDate(List<Long> pickupIds, List<Long> deliveryIds, boolean locationFromNext){

        List<Pickup> pickups = new ArrayList<>();
        List<Delivery> deliveries = new ArrayList<>();

        for(Long id: pickupIds){
            pickups.add(pickupService.getById(id));
        }

        for(Long id: deliveryIds){
            deliveries.add(deliveryService.getById(id));
        }

        List<Pair<Long, Long>> idsAndTimes = new ArrayList<>(pickups.size() + deliveries.size());

        for(Pickup pickup: pickups){
            idsAndTimes.add(Pair.of(pickup.getId(), pickup.getPickupDate().getTime()));
        }
        for(Delivery delivery: deliveries){
            idsAndTimes.add(Pair.of(delivery.getId(), delivery.getDeliveryDate().getTime()));
        }

        for(int i = 0; i < idsAndTimes.size() - 1; i++){
            if(idsAndTimes.get(i).getSecond() > idsAndTimes.get(i + 1).getSecond()){
                Collections.swap(idsAndTimes, i, i + 1);
                i = -1;
            }
        }

        List<Object> data = new ArrayList<>();
        String pattern = "MM-dd-YYYY HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        for(Pair<Long,Long> idAndTime: idsAndTimes){
            if(pickupService.existsById(idAndTime.getFirst())) {
                Pickup pickup = pickupService.getById(idAndTime.getFirst());
                PickupDto pickupDto = modelMapper.map(pickupService.getById(idAndTime.getFirst()), PickupDto.class);
                Date date = new Date(idAndTime.getSecond());
                pickupDto.setPickupDateFormatted(simpleDateFormat.format(date));
                Company shipper = companyService.getById(pickup.getShipperCompanyId());

                if(shipper.getLocationId() != null) {
                    if(!locationFromNext)
                        pickupDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ", " + resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                    else
                        pickupDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ",|" + resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                }

                data.add(pickupDto);

            }
            if(deliveryService.existsById(idAndTime.getFirst())){
                Delivery delivery =  deliveryService.getById(idAndTime.getFirst());
                DeliveryDto deliveryDto = modelMapper.map(delivery, DeliveryDto.class);
                Date date = new Date(idAndTime.getSecond());
                deliveryDto.setDeliveryDateFormatted(simpleDateFormat.format(date));

                Company shipper = companyService.getById(delivery.getConsigneeCompanyId());
                if(shipper.getLocationId() != null) {
                    if(!locationFromNext)
                        deliveryDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ", " + resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                    else
                        deliveryDto.setConsigneeNameAndLocation(shipper.getCompanyName() + ",|" + resolveLocationNameAndParentAbbreviation(shipper.getLocationId()));
                }

                data.add(deliveryDto);
            }
        }
        return data;
    }

    public Pair<PickupDto, DeliveryDto> getFirstAndLastStops(Long firstStopId, Long secondStopId){

        Pickup pickup = pickupService.getById(firstStopId);
        PickupDto pickupDto = new PickupDto();
        pickupDto.setConsigneeNameAndLocation(resolveShipper(pickup.getShipperCompanyId()));

        Delivery delivery = deliveryService.getById(secondStopId);
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setConsigneeCompanyId(delivery.getConsigneeCompanyId());
        deliveryDto.setDeliveryDate(delivery.getDeliveryDate());
        deliveryDto.setConsigneeNameAndLocation(resolveShipper(delivery.getConsigneeCompanyId()));

        return Pair.of(pickupDto, deliveryDto);
    }

    public Pair<PickupDto, DeliveryDto>  getFirstAndLastStopsOnlyLocations(Long firstStopId, Long secondStopId){
        Pickup pickup = pickupService.getById(firstStopId);
        PickupDto pickupDto = new PickupDto();
        pickupDto.setConsigneeNameAndLocation(resolveShipperOnlyLocation(pickup.getShipperCompanyId()));

        Delivery delivery = deliveryService.getById(secondStopId);
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setConsigneeCompanyId(delivery.getConsigneeCompanyId());
        deliveryDto.setDeliveryDate(delivery.getDeliveryDate());
        deliveryDto.setConsigneeNameAndLocation(resolveShipperOnlyLocation(delivery.getConsigneeCompanyId()));

        return Pair.of(pickupDto, deliveryDto);
    }

    public Pair<PickupDto, DeliveryDto>  getFirstAndLastStopsOnlyLocations2(Long firstStopId, Long secondStopId){
        Pickup pickup = pickupService.getById(firstStopId);
        PickupDto pickupDto = new PickupDto();
        pickupDto.setShipperCompanyId(pickup.getShipperCompanyId());
        pickupDto.setPickupDate(pickup.getPickupDate());
        pickupDto.setConsigneeNameAndLocation(resolveShipperOnlyLocation(pickup.getShipperCompanyId()));

        Delivery delivery = deliveryService.getById(secondStopId);
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setConsigneeCompanyId(delivery.getConsigneeCompanyId());
        deliveryDto.setDeliveryDate(delivery.getDeliveryDate());
        deliveryDto.setConsigneeNameAndLocation(resolveShipperOnlyLocation(delivery.getConsigneeCompanyId()));

        return Pair.of(pickupDto, deliveryDto);
    }

    public List<Long> getVisibleIds(User user){
        if(user == null)
            return ownedCompanyService.getIds();

        List<Long> visibleIds = user.getVisibleIds();
        if(visibleIds == null || visibleIds.isEmpty())
            return ownedCompanyService.getIds();
        else return visibleIds;
    }

    public List<Long> getVisibleTeamIds(User user){
        if(user == null)
            return teamRepository.findAllIds();

        if(user.getVisibleTeamIds() == null || user.getVisibleTeamIds().isEmpty())
            return teamRepository.findAllIds();

        return user.getVisibleTeamIds();

    }

    public Boolean validPickupMinAndDeliveryMaxTimes(Long pickupMinTime, Long deliveryMinTime){
        Long currentTime = new Date().getTime();
        return currentTime < pickupMinTime && currentTime < deliveryMinTime;
    }

    public String resolveLocationNameAndParentAbbreviation(Long locationId){
        Location location = locationService.findById(locationId);
        if(location != null)
            return location.getName() + ", " + location.getParentAnsi();
        else return "";
    }

    public Pair<PickupDto, DeliveryDto> firstPickupAndLastDelivery(List<Long> pickupIds, List<Long> deliveryIds){

        List<Object> pickupsAndDeliveries = sortByPickupOrDeliveryDate(pickupIds, deliveryIds, false);

        PickupDto pickupDto = (PickupDto)pickupsAndDeliveries.get(0);
        DeliveryDto deliveryDto = (DeliveryDto)pickupsAndDeliveries.get(pickupsAndDeliveries.size() - 1);

        return Pair.of(pickupDto, deliveryDto);
    }


    public List<Map<String, Object>> createSequence(List<Long> pickupIds, List<Long> deliveryIds){
        Set<Long> times = getTimesOfPickupsAndDeliveries(pickupIds, deliveryIds);

        Long currentTime = getTimeStampWithOffset();
        List<Map<String, Object>> stageSequence = new ArrayList<>();
        int i = 1;

        for(Long time: times){
            Map<String, Object> idAndValue = new HashMap<String, Object>();
            idAndValue.put("id", i);

            if(time <= currentTime)
                idAndValue.put("value", true);
            else
                idAndValue.put("value", false);

            stageSequence.add(idAndValue);
            i++;
        }

        return stageSequence;
    }

    private Set<Long> getTimesOfPickupsAndDeliveries(List<Long> pickupIds, List<Long> deliveryIds){
        Set<Long> times = new TreeSet<>();

        for(Long id: pickupIds){
            Pickup pickup = pickupService.getById(id);
            times.add(pickup.getPickupDate().getTime());
        }
        for(Long id: deliveryIds){
            Delivery delivery = deliveryService.getById(id);
            times.add(delivery.getDeliveryDate().getTime());
        }
        return times;
    }

    public Long resolveTimeForTrip(Long timeStart, Long timeEnd){

        Long currentTime = getTimeStampWithOffset();

        if(currentTime < timeStart)
            return 1L;
        else if(currentTime < timeEnd)
            return 2L;
        else return 3L;

    }

    public Long getTimeStampWithOffset(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -11);
        return calendar.getTimeInMillis();
    }

    private String resolveShipper(Long shipperCompanyId){

        Company shipper = companyService.getById(shipperCompanyId);
        String text = "";
        if(shipper.getLocationId() != null) {
                text = shipper.getCompanyName() + ", " + resolveLocationNameAndParentAbbreviation(shipper.getLocationId());
        }
        return text;
    }

    private String resolveShipperOnlyLocation(Long shipperCompanyId){
        Company shipper = companyService.getById(shipperCompanyId);
        if(shipper.getLocationId() != null) {
            return resolveLocationNameAndParentAbbreviation(shipper.getLocationId());
        }
        return "";
    }




}
