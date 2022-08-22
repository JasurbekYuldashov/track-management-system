package uz.binart.trackmanagementsystem.service.impl.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.UnitType;
import uz.binart.trackmanagementsystem.repository.type.UnitTypeRepository;
import uz.binart.trackmanagementsystem.service.type.UnitTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnitTypeServiceImpl implements UnitTypeService {

    private UnitTypeRepository unitTypeRepository;
    private Map<Long, UnitType> unitTypeMap;

    @Autowired
    public UnitTypeServiceImpl(UnitTypeRepository unitTypeRepository){
        this.unitTypeRepository = unitTypeRepository;
        unitTypeMap = new HashMap<>();
        List<UnitType> unitTypes = unitTypeRepository.findAll();
        for(UnitType unitType: unitTypes){
            unitTypeMap.put(unitType.getId(), unitType);
        }
    }

    public Boolean existsById(Long id){
        return unitTypeRepository.existsById(id);
    }

    public UnitType getFromManualCache(Long id){
        return unitTypeMap.get(id);
    }

    public List<UnitType> getAll(){
        return unitTypeRepository.findAll();
    }

    public UnitType getById(Long id){
        return unitTypeRepository.getOne(id);
    }

}
