package uz.binart.trackmanagementsystem.service.status;

import uz.binart.trackmanagementsystem.model.status.UnitStatus;

import java.util.List;
import java.util.Set;

public interface UnitStatusService {

    UnitStatus findById(Long id);

    UnitStatus getFromManualCache(Long id);

    List<UnitStatus> getAll();

    Boolean existsById(Long id);

    Set<Long> exclusionStatuses();

}
