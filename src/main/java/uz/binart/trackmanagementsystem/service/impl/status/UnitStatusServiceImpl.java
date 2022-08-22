package uz.binart.trackmanagementsystem.service.impl.status;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;
import uz.binart.trackmanagementsystem.repository.status.UnitStatusRepository;
import uz.binart.trackmanagementsystem.service.status.UnitStatusService;

import java.util.*;

@Slf4j
@Service
public class UnitStatusServiceImpl implements UnitStatusService {

    private UnitStatusRepository unitStatusRepository;
    private Map<Long, UnitStatus> unitStatusMap;

    @Autowired
    public UnitStatusServiceImpl(UnitStatusRepository unitStatusRepository){
        this.unitStatusRepository = unitStatusRepository;
        unitStatusMap = new HashMap<>();
        List<UnitStatus> unitStatuses = unitStatusRepository.findAll();
        for(UnitStatus unitStatus: unitStatuses){
            unitStatusMap.put(unitStatus.getId(), unitStatus);
        }
    }

    public UnitStatus findById(Long id){

        Optional<UnitStatus> opt = unitStatusRepository.findById(id);

        if(opt.isPresent()){
            return opt.get();
        }
        else {
            log.warn("Not found in " + this.getClass() + " service");
            throw new NotFoundException();
        }
    }

    public UnitStatus getFromManualCache(Long id){
        return unitStatusMap.get(id);
    }


    public List<UnitStatus> getAll(){
        return unitStatusRepository.findAll();
    }

    public Boolean existsById(Long id){
        return unitStatusRepository.existsById(id);
    }

    public Set<Long> exclusionStatuses(){
        return unitStatusRepository.getProperStatuses(Lists.newArrayList(7L, 5L, 4L, 6L, 8L));
    }


}
