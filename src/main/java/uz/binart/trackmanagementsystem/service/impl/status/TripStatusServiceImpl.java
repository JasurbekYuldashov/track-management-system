package uz.binart.trackmanagementsystem.service.impl.status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.status.TripStatus;
import uz.binart.trackmanagementsystem.repository.status.TripStatusRepository;
import uz.binart.trackmanagementsystem.service.status.TripStatusService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripStatusServiceImpl implements TripStatusService {

    private final TripStatusRepository tripStatusRepository;

    public TripStatus findById(Long id){
        if(!existsById(id)){
            log.warn("no status with " + id + " found in database");
            throw new NotFoundException();
        }
        return tripStatusRepository.getOne(id);
    }

    public List<TripStatus> getAll(){
        return tripStatusRepository.findAll();
    }

    public Boolean existsById(Long id){
        return tripStatusRepository.existsById(id);
    }

}
