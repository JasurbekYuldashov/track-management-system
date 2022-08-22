package uz.binart.trackmanagementsystem.service;

import org.springframework.data.util.Pair;
import uz.binart.trackmanagementsystem.dto.DeliveryDto;
import uz.binart.trackmanagementsystem.dto.PickupDto;
import uz.binart.trackmanagementsystem.model.User;

import java.util.List;
import java.util.Map;

public interface UtilService {

    List<Object> sortByPickupOrDeliveryDate(List<Long> pickupIds, List<Long> deliveryIds, boolean locationFromNext);

    Boolean validPickupMinAndDeliveryMaxTimes(Long pickupMinTime, Long deliveryMinTime);

    String resolveLocationNameAndParentAbbreviation(Long locationId);

    Long resolveTimeForTrip(Long timeStart, Long timeEnd);

    Pair<PickupDto, DeliveryDto> firstPickupAndLastDelivery(List<Long> pickupIds, List<Long> deliveryIds);

    List<Map<String, Object>> createSequence(List<Long> pickupIds, List<Long> deliveryIds);

    Long getTimeStampWithOffset();

    Pair<PickupDto, DeliveryDto> getFirstAndLastStops(Long firstStopId, Long secondStopId);

    Pair<PickupDto, DeliveryDto> getFirstAndLastStopsOnlyLocations(Long firstStopId, Long secondStopId);

    Pair<PickupDto, DeliveryDto>  getFirstAndLastStopsOnlyLocations2(Long firstStopId, Long secondStopId);

    List<Long> getVisibleIds(User user);

    List<Long> getVisibleTeamIds(User user);

}
