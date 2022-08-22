package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.DriverType;
import uz.binart.trackmanagementsystem.repository.type.DriverTypeRepository;
import uz.binart.trackmanagementsystem.service.type.DriverTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverTypeServiceImpl implements DriverTypeService {

    private final DriverTypeRepository driverTypeRepository;

    public DriverType getById(Long id){
        return driverTypeRepository.getOne(id);
    }

    public List<DriverType> getAll(){
        return driverTypeRepository.findAll();
    }

    public Boolean existsById(Long id){
        return driverTypeRepository.existsById(id);
    }

}
