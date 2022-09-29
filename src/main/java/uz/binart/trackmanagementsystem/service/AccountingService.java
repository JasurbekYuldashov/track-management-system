package uz.binart.trackmanagementsystem.service;

import org.springframework.boot.configurationprocessor.json.JSONException;
import uz.binart.trackmanagementsystem.dto.AccountingDto;
import uz.binart.trackmanagementsystem.model.Load;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountingService {

    List<Long> matchedIds();

    List<Load> findLoads();

    List<AccountingDto> getProperInfo(Long carrierId, Long driverId, Long truckId, Long teamId, Long allByCompanySTruck, LocalDateTime dateTime, Boolean monthly, Boolean isDispatcher) throws JSONException;

    List<AccountingDto> getProperInfoReport(Long carrierId, Long driverId, Long truckId, Long teamId, Long allByCompanySTruck, Long startTime, Long endTime, Boolean monthly, Boolean isDispatcher) throws JSONException;

}
