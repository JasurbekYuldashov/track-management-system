package uz.binart.trackmanagementsystem.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.service.LocationService;
import uz.binart.trackmanagementsystem.service.TimeService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TimeServiceImpl implements TimeService {

    private final LocationService locationService;

    public Pair<Long, Long> getCorrectedByCentralTimeTimestamps(Date localStartTime, Date localEndTime, Company shipper, Company consignee){
        Long correctedStartTime = resolveTime(localStartTime, shipper);
        Long correctedEndTime = resolveTime(localEndTime, consignee);
        return Pair.of(correctedStartTime, correctedEndTime);
    }

    private Long resolveTime(Date date, Company company){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer timeZone = -6;
        Integer central = -6;
        if(company.getLocationId() != null){

            Location location = locationService.findById(company.getLocationId());
            if(location != null){
                Location parentLocation = locationService.findByAnsiFromCache(location.getParentAnsi());
                if(parentLocation != null){

                    if(location.getParentTimeZone().equals(1))
                        timeZone = parentLocation.getFirstTimeZone();
                    else if(location.getParentTimeZone().equals(2))
                        timeZone = parentLocation.getSecondTimeZone();
                }

            }
        }

        int dif = central - timeZone;

        calendar.add(Calendar.HOUR_OF_DAY, dif);

        return calendar.getTimeInMillis();
    }


}
