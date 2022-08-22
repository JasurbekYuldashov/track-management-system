package uz.binart.trackmanagementsystem.service.impl;

import io.grpc.internal.JsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.AccountingDto;
import uz.binart.trackmanagementsystem.model.*;
import uz.binart.trackmanagementsystem.service.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {

    private final LoadService loadService;
    private final TripService tripService;
    private final OwnedCompanyService ownedCompanyService;
    private final UtilService utilService;
    private final PickupService pickupService;
    private final DeliveryService deliveryService;
    private final CompanyService companyService;
    private final UnitService unitService;
    private final TeamService teamService;

    public List<Long> matchedIds(){
        return null;
    }

    public List<Load> findLoads(){

        return null;
    }

    public List<AccountingDto> getProperInfo(Long carrierId, Long truckId, Long driverId, Long teamId, Long allByCompanysTruck,  Long startTime, Long endTime, Boolean weekly, Boolean isDispatcher){
        List<Long> segments = getSegments(startTime, endTime);
        List<Load> loads = loadService.findAllForAccounting(carrierId, truckId, driverId, teamId, allByCompanysTruck, startTime, endTime, weekly);

        List<AccountingDto> accountingDtoList = new ArrayList<>();
        for(int i = 0; i < loads.size(); i++){
            AccountingDto acc = new AccountingDto();
            acc.setSerialNumber(i + 1);
            Load load = loads.get(i);

            Optional<Trip> tripOptional = tripService.getById(load.getTripId());

            if(tripOptional.isPresent()){
                Trip trip = tripOptional.get();
                Unit unit = getTruck(trip);
                if(unit != null) {
                    acc.setTruckNumber(unit.getNumber());
                    if (unit.getEmployerId() != null)
                        acc.setTruckCompany(ownedCompanyService.getFromCache(unit.getEmployerId()).getName());
                    if (unit.getTeamId() != null) {
                        acc.setTeam(teamService.getById(unit.getTeamId()).getName());
                    }
                }
                acc.setCarrierName(getCarrierName(trip));
            }

            acc.setRc(load.getCustomLoadNumber());

            Company company = companyService.getById(load.getCustomerId());

            if(company != null)
                acc.setCompany(company.getCompanyName());

            Company shipperCompany = getShipperOrConsigneeCompany(load, true);
            if(shipperCompany != null) {
                Map<String, String> shipperNameAndLocation = getShipperCompanyNameAndLocationWithZip(shipperCompany);
                acc.setShipperCompanyName(shipperNameAndLocation.get("company_name"));
                if(!shipperNameAndLocation.get("zip").equals(""))
                    acc.setShipperCompanyLocation(shipperNameAndLocation.get("company_location") + " " + shipperNameAndLocation.get("zip"));
                else
                    acc.setShipperCompanyLocation(shipperNameAndLocation.get("company_location"));
            }
            acc.setTimeStart(load.getCentralStartTime() != null ? load.getCentralStartTime() : load.getStartTime());

            acc.setEndTime(load.getCentralEndTime() != null ? load.getCentralEndTime() : load.getEndTime());
            Company consigneeCompany = getShipperOrConsigneeCompany(load, false);
            if(consigneeCompany != null) {
                Map<String, String> consigneeCompanyNameAndLocation = getShipperCompanyNameAndLocationWithZip(consigneeCompany);
                acc.setEndLocation(consigneeCompanyNameAndLocation.get("company_location"));
                if(!consigneeCompanyNameAndLocation.get("zip").equals(""))
                    acc.setEndLocation(consigneeCompanyNameAndLocation.get("company_location") + " " + consigneeCompanyNameAndLocation.get("zip"));
                else
                    acc.setEndLocation(consigneeCompanyNameAndLocation.get("company_location"));
            }

            acc.setFine(load.getFine() != null ? load.getFine() : 0f);
            acc.setBooked(load.getBooked() != null ? load.getBooked() : 0f);
            acc.setDispute(load.getDispute() != null ? load.getDispute() : 0f);
            acc.setDetention(load.getDetention() != null ? load.getDetention() : 0f);
            acc.setAdditional(load.getAdditional() != null ? load.getAdditional() : 0f);
            acc.setRevisedInvoice(load.getRevisedInvoice() != null ? load.getRevisedInvoice() : 0f);

            float amount = load.getRcPrice() != null ? load.getRcPrice() : 0f, revised = load.getRevisedRcPrice() != null ? load.getRevisedRcPrice() : 0f;
            float factoring = load.getFactoring() != null ? load.getFactoring() : 0f;
            float tafs = load.getTafs() != null ? load.getTafs() : 0f;

            if(revised != 0f)
                acc.setKo(revised - amount);
            else
                acc.setKo(0f);

            if(!isDispatcher) {
                acc.setFactoring(factoring);
                acc.setTafs(tafs);
                acc.setNetPaid(load.getNetPaid() != null ? load.getNetPaid() : 0f);
            }else{
                acc.setFactoring(0f);
                acc.setTafs(0f);
                acc.setNetPaid(0f);
            }


            try {
                acc.setDateTime(acc.getEndTime().toString());
            } catch (Exception e){}


            //------------------------------setting segmented price-----------------------------------------

            Long timeStart = load.getCentralStartTime() != null ? load.getCentralStartTime() : load.getStartTime();
            Long timeEnd = load.getCentralEndTime() != null ? load.getCentralEndTime() : load.getEndTime();

            Pair<Integer, Integer> startsBeforeAndEndsAfter = getStartBeforeAndEndsAfterIndexes(segments, timeStart, timeEnd);
            Integer startsBefore = startsBeforeAndEndsAfter.getFirst();
            Integer endsAfter = startsBeforeAndEndsAfter.getSecond();

            if(endsAfter != -1){

                long startsBeforePoint;

                if(startsBefore != -1)
                    startsBeforePoint = segments.get(startsBefore);
                else startsBeforePoint = 0;

                long endsAfterPoint = segments.get(endsAfter);

                long start = timeStart;
                long end = timeEnd;

                Map<String, Long> periods = countEntireBeforeAndAfterInMinutes(startsBeforePoint, endsAfterPoint, start, end, startsBefore, endsAfter);
                long entire = periods.get("entire");
                long beforeStart = periods.get("beforeStart");
                long afterEnd = periods.get("afterEnd");

                double price = load.getRevisedRcPrice() != null ? load.getRevisedRcPrice() : load.getRcPrice();
                double costOfAMinute = price / entire;

                double[] sorted = sortByWeeks(segments.size(), startsBefore, endsAfter, costOfAMinute * beforeStart, costOfAMinute * afterEnd, entire * costOfAMinute);

                acc.setSegmentedPrices(sorted);

            }else{
                double[] sorted = new double[segments.size()];
                double price = load.getRevisedRcPrice() != null ? load.getRevisedRcPrice() : load.getRcPrice();
                DecimalFormat df = new DecimalFormat("#.##");
                try {
                    sorted[0] = Double.parseDouble(df.format(price));
                }catch(NumberFormatException exception){
                    try {
                        DecimalFormat df2 = new DecimalFormat("#,##");
                        sorted[0] = Double.parseDouble(df2.format(price));
                    }catch(NumberFormatException exception2){
                        DecimalFormat df3 = new DecimalFormat("# ##");
                        sorted[0] = Double.parseDouble(df3.format(price));
                    }
                }
                acc.setSegmentedPrices(sorted);
            }

            //----------------------------------------------------------------------------------------------
            accountingDtoList.add(acc);
        }
//sout
        return accountingDtoList;
    }

    private String getCarrierName(Trip trip){
        OwnedCompany ownedCompany = ownedCompanyService.getFromCache(trip.getEmployerId());
        return ownedCompany != null ? ownedCompany.getAbbreviation() : "";
    }

    private Map<String, String> getShipperCompanyNameAndLocationWithZip(Company company){
        Map<String, String> map = new HashMap<>();
        if(company.getLocationId() != null) {
            map.put("company_name", company.getCompanyName());
            map.put("company_location", utilService.resolveLocationNameAndParentAbbreviation(company.getLocationId()));
            map.put("zip", company.getZipCode() != null ? company.getZipCode() : "");
        }else{
            map.put("company_name", "");
            map.put("company_location", "");
            map.put("zip", "");
        }
        return map;
    }

    private List<Long> getSegments(Long timeStart, Long timeEnd){

        List<Long> segments = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStart);

        if(calendar.get(Calendar.DAY_OF_WEEK) >= 6)
            calendar.add(Calendar.WEEK_OF_MONTH, 1);

        calendar.set(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        do{
            segments.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_WEEK, 7);
        }while(calendar.getTimeInMillis() <= timeEnd);

        return segments;
    }



    private Company getShipperOrConsigneeCompany(Load load, Boolean shipper){

        List<Long> ids = load.getSortedPickupAndDeliveryIds();

        if(ids !=  null && ids.size() >= 2){
            if(shipper){
                Pickup pickup = pickupService.getById(ids.get(0));
                return companyService.getById(pickup.getShipperCompanyId());
            }else{
                Delivery delivery = deliveryService.getById(ids.get(ids.size() - 1));
                return companyService.getById(delivery.getConsigneeCompanyId());
            }
        }
        return null;
    }

    private Unit getTruck(Trip trip){
        if(trip.getTruckId() != null){
            Optional<Unit> unitOptional = unitService.findById(trip.getTruckId());
            return unitOptional.orElse(null);
        }
        return null;
    }

    private Pair<Integer, Integer> getStartBeforeAndEndsAfterIndexes(List<Long> segments, Long timeStart, Long timeEnd){

        int startsBefore = -1, endsAfter = -1;
        for(int i = 0; i < segments.size(); i++){
            if(timeStart < segments.get(i) && startsBefore == -1){
                startsBefore = i;
            }

            if(timeEnd > segments.get(i)){
                endsAfter = i;
            }
        }

        return Pair.of(startsBefore, endsAfter);
    }

    private Map<String, Long> countEntireBeforeAndAfterInMinutes(long startPoint, long endPoint, long start, long end, int segmentIndexStart, int segmentIndexEnd){

        long entirePeriod = end - start;

        long beforeStart, afterEnd;
        if(segmentIndexStart == -1){
            beforeStart = end - start;
            afterEnd = end - start;
        }else if(segmentIndexStart - segmentIndexEnd != 1) {
            beforeStart = startPoint - start;
            afterEnd = end - endPoint;
        }else{
            beforeStart = afterEnd = entirePeriod;
        }

        long entirePeriodInSeconds = entirePeriod;
        long beforeStartInSeconds = beforeStart;
        long afterEndInSeconds = afterEnd;

        Map<String, Long> map = new HashMap<>();
        map.put("entire", entirePeriodInSeconds);
        map.put("beforeStart", beforeStartInSeconds);
        map.put("afterEnd", afterEndInSeconds);

        return map;

    }

    private double[] sortByWeeks(int numberOfSegments, int distributionStart, int distributionEnd, double priceBefore, double priceAfter, double entirePrice){

        DecimalFormat df1 = new DecimalFormat("#.##");
        DecimalFormat df2 = new DecimalFormat("#,##");
        DecimalFormat df3 = new DecimalFormat("# ##");

        double[] sortedPrices = new double[numberOfSegments + 1];

        if(priceAfter == priceBefore && priceBefore == entirePrice && distributionStart == distributionEnd){
            sortedPrices[distributionStart] = tryParseDouble(df1, df2, df3, entirePrice);
        }else if(distributionStart > distributionEnd){
            sortedPrices[distributionStart] = tryParseDouble(df1, df2, df3, entirePrice);
        }
        else if(distributionStart == distributionEnd) {
            sortedPrices[distributionStart] = tryParseDouble(df1, df2, df3, priceBefore);
            sortedPrices[distributionEnd + 1] = tryParseDouble(df1, df2, df3, priceAfter);
        }else if( distributionEnd - distributionStart == 1){
            if(distributionStart == -1 && distributionEnd == 0){
                distributionStart++;
                distributionEnd++;
            }
            sortedPrices[distributionStart] = tryParseDouble(df1, df2, df3, priceBefore);
            sortedPrices[distributionEnd] = tryParseDouble(df1, df2, df3, priceAfter);
        }else if(distributionStart == -1){
            sortedPrices[distributionEnd] = tryParseDouble(df1, df2, df3, priceAfter);
        }else {
            sortedPrices[distributionStart] = tryParseDouble(df1, df2, df3, priceBefore);
            double leftPrice = entirePrice - priceBefore - priceAfter;
            double leftSegments = distributionEnd - distributionStart - 1;
            double perSegment = tryParseDouble(df1, df2, df3, (leftPrice / leftSegments));
            for(int i = distributionStart + 1; i < distributionEnd; i++){
                sortedPrices[i] = perSegment;
            }
            sortedPrices[distributionEnd] = tryParseDouble(df1, df2, df3, priceAfter);
        }

        return sortedPrices;
    }

    private double tryParseDouble(DecimalFormat df1, DecimalFormat df2, DecimalFormat df3, double number){
        double result;
        try {
            result = Double.parseDouble(df1.format(number));
        }catch(NumberFormatException exception){
            try {
                result = Double.parseDouble(df2.format(number));
            }catch (NumberFormatException exception2){
                try {
                    result = Double.parseDouble(df3.format(number));
                }catch(NumberFormatException exception3){
                    result = Double.parseDouble(df1.format( 0.0F));
                }
            }
        }
        return result;
    }


}
