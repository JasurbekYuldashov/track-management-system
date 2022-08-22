package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.service.StatusService;
import uz.binart.trackmanagementsystem.service.UtilService;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final UtilService utilService;

    public Long resolveTimeForUnit(Long timeStart, Long timeEnd){
        Long currentTime = utilService.getTimeStampWithOffset();

        if(currentTime < timeStart)
            return 2L;
        else if(currentTime < timeEnd)
            return 1L;
        else return 3L;

    }




}
