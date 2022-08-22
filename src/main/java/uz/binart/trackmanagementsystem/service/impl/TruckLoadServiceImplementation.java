package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.repository.LoadRepository;
import uz.binart.trackmanagementsystem.repository.UnitRepository;
import uz.binart.trackmanagementsystem.service.TruckLoadService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckLoadServiceImplementation implements TruckLoadService {

    private final UnitRepository unitRepository;
    private final LoadRepository loadRepository;

    public List<String> getTableHeaders(@NotNull Long start, @NotNull Long end){

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(start);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(end);

        List<String> headers = new ArrayList<>();

        while(startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()){
            int dayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH);
            int month = startCalendar.get(Calendar.MONTH);
            int numberOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);
            headers.add(String.format("%02d/%02d", dayOfMonth, month + 1));
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return headers;
    }





}
