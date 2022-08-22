package uz.binart.trackmanagementsystem.service;

import uz.binart.trackmanagementsystem.dto.AccountingDto;
import uz.binart.trackmanagementsystem.model.Load;

import java.util.List;

public interface AccountingService {

    List<Long> matchedIds();

    List<Load> findLoads();

    List<AccountingDto> getProperInfo(Long carrierId, Long driverId, Long truckId, Long teamId, Long allByCompanySTruck, Long startTime, Long endTime, Boolean monthly, Boolean isDispatcher);

}
