package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.time.temporal.ChronoField;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final TripService tripService;
    private final LoadService loadService;
    private final UnitService unitService;
    private final UnitStatusService unitStatusService;
    private final DriverService driverService;
    private final ExpirationNotificationService expirationNotificationService;


    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedRate = 30000)
    public void updateTripAndUnitStatuses() throws JSONException {

//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.HOUR_OF_DAY, -11);
//        Long currentTime = calendar.getTimeInMillis();
//        Long currentTime = new Date().getTime();
//        System.out.println(currentTime);
//        System.out.println(new Date().getTime());

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

        setStatuses(loadService.findAfter(currentTime), 3L, 3L, true, false, false, true, currentTime);

        setStatuses(loadService.findUpcoming(currentTime), 1L, 2L, false,  true, false,false, currentTime);

        setStatuses(loadService.findBetween(currentTime), 2L, 1L, false, false, true, false, currentTime);

        setAsReady(unitService.findAllWithExpiredEld(currentTime), currentTime);

    }

    @Scheduled(fixedRate = 60000)
    public void trackExpirationOfDriversDocuments(){

    }

    @Scheduled(fixedRate = 60000)
    public void trackExpirationOfUnitDocuments(){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -11);
        calendar.add(Calendar.MONTH, -1);

        Long monthAgoCentralTime = calendar.getTimeInMillis();

        List<Unit> expiredByLicence = findExpiredByLicencePlate(monthAgoCentralTime);
        createAndSaveNotifications(expiredByLicence, "License expiration", true, null, null);

        List<Unit> expiredByAnnualInspection = findExpiredByAnnualInspection(monthAgoCentralTime);
        createAndSaveNotifications(expiredByAnnualInspection, "annual inspection", null, true, null);

        List<Unit> expiredByInspectionSticker = findExpiredByInspectionStickerExpiration(monthAgoCentralTime);
        createAndSaveNotifications(expiredByInspectionSticker, "inspection sticker expiration", null, null, true);

    }

    private void setStatuses(List<Load> loads, Long tripStatusId, Long unitStatusId, Boolean unbind, Boolean upcoming, Boolean covered, Boolean history, Long currentTime){

        for(Load load: loads){
//            System.out.println(tripStatusId);
            Optional<Trip> tripOptional = tripService.getById(load.getTripId());
            if(tripOptional.isPresent()){
                Trip trip = tripOptional.get();

                if(trip.getDeleted())
                    continue;


                if(trip.getTripStatusId() == null) {
                    trip.setTripStatusId(tripStatusId);
                    tripService.update(trip);
                }
                else if(trip.getTripStatusId() != null && !trip.getTripStatusId().equals(tripStatusId)) {
                    trip.setTripStatusId(tripStatusId);
                    tripService.update(trip);
                }
                Unit unit = null;
                try {
                    unit = unitService.getById(trip.getTruckId());
                } catch (Exception exception){
                    System.out.println(trip.getId());
                    System.out.println(exception);
                }
                Set<Long> unitStatuses = unitStatusService.exclusionStatuses();

//                if(unit != null){
                if(unit != null){

                    try{
                        if(!unitStatuses.contains(unit.getUnitStatusId()))
                            continue;
                    } catch (Exception e){}

                    unit.setUnitStatusId(unitStatusId);

                    if(trip.getEmployerId() != null){
                        if(!unbind)
                            unit.setCurrentEmployerId(trip.getEmployerId());
                        else
                            unit.setCurrentEmployerId(null);
                        if(history) {
                            unit.setLastTripId(null);

                            unit.setReadyFrom(currentTime);
                            unit.setLastCompletedTripId(trip.getId());
                        }
                        else {
                            unit.setLastTripId(trip.getId());
                            unit.setReadyFrom(null);
                        }
                    }

                    if(upcoming || covered)
                        unit.setReadyFrom(null);

                    unitService.update(unit);
                }

            }

            load.setUpdatedAsUpcoming(upcoming);
            load.setUpdatedAsCovered(covered);
            load.setUpdatedAsHistory(history);

            loadService.save(load);
        }
    }

    private void setAsReady(List<Unit> units, Long currentTime){
        for(Unit unit: units){
            unit.setUnitStatusId(3L);
            unit.setReadyFrom(currentTime);
            unit.setEldUnTil(null);
            unitService.update(unit);
        }
    }

    private List<Unit> findExpiredByLicencePlate(Long time){
        return unitService.findByLicensePlateExpirationAfter(time);
    }

    private List<Unit> findExpiredByAnnualInspection(Long time){
        return unitService.findByAnnualInspectionExpirationTimeAfter(time);
    }

    private List<Unit> findExpiredByInspectionStickerExpiration(Long time){
        return unitService.findByAnnualInspectionExpirationTimeAfter(time);
    }

    private void createAndSaveNotifications(List<Unit> units, String expirationFieldName, Boolean notifiedOfLicensePlate, Boolean notifiedOfAnnualInspection, Boolean notifiedOfRegistrationExpiration){
        for(Unit unit: units){
            ExpirationNotification expirationNotification = new ExpirationNotification();
            expirationNotification.setExpirationEntityLink(unit.getNumber());
            expirationNotification.setExpirationFieldName(expirationFieldName);

            if(notifiedOfLicensePlate != null && notifiedOfLicensePlate){
                expirationNotification.setExpirationTime(unit.getLicensePlateExpirationTime());
            }
            if(notifiedOfAnnualInspection != null && notifiedOfAnnualInspection){
                expirationNotification.setExpirationTime(unit.getAnnualInspectionExpirationTime());
            }
            if(notifiedOfRegistrationExpiration != null && notifiedOfRegistrationExpiration){
                expirationNotification.setExpirationTime(unit.getRegistrationExpirationTime());
            }

            expirationNotificationService.save(expirationNotification);
            if(notifiedOfLicensePlate != null) unit.setNotifiedOfLicensePlateExpiration(notifiedOfLicensePlate);
            if(notifiedOfAnnualInspection != null) unit.setNotifiedOfInspection(notifiedOfAnnualInspection);
            if(notifiedOfRegistrationExpiration != null) unit.setNotifiedOfRegistration(notifiedOfRegistrationExpiration);

            unitService.update(unit);
        }
    }

}
