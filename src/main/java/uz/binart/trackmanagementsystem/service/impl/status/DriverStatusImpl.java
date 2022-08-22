package uz.binart.trackmanagementsystem.service.impl.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.status.DriverStatus;
import uz.binart.trackmanagementsystem.repository.status.DriverStatusRepository;
import uz.binart.trackmanagementsystem.service.status.DriverStatusService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DriverStatusImpl implements DriverStatusService {

    private DriverStatusRepository driverStatusRepository;
    private Map<Long, DriverStatus> driverStatusMap;

    @Autowired
    public DriverStatusImpl(DriverStatusRepository driverStatusRepository){
        this.driverStatusRepository = driverStatusRepository;
        driverStatusMap = new HashMap<>();
        List<DriverStatus> driverStatuses = driverStatusRepository.findAll();
        for(DriverStatus driverStatus: driverStatuses){
            driverStatusMap.put(driverStatus.getId(), driverStatus);
        }
    }


    public List<DriverStatus> getAll() {
        return driverStatusRepository.findAll();
    }

    public Optional<DriverStatus> findById(Long id){
        return driverStatusRepository.findById(id);
    }

    public DriverStatus getFromManualCache(Long id){
        return driverStatusMap.get(id);
    }

}
