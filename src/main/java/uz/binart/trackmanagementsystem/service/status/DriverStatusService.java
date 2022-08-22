package uz.binart.trackmanagementsystem.service.status;

import uz.binart.trackmanagementsystem.model.status.DriverStatus;

import java.util.List;
import java.util.Optional;

public interface DriverStatusService {

    List<DriverStatus> getAll();

    Optional<DriverStatus> findById(Long id);

    DriverStatus getFromManualCache(Long id);

}

