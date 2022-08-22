package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.QuantityType;
import uz.binart.trackmanagementsystem.repository.type.QuantityTypeRepository;
import uz.binart.trackmanagementsystem.service.type.QuantityTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuantityTypeServiceImpl implements QuantityTypeService {

    private final QuantityTypeRepository quantityTypeRepository;

    public List<QuantityType> getAll(){
        return quantityTypeRepository.findAll();
    }

    public Boolean existsById(Long id){
        return quantityTypeRepository.existsById(id);
    }

}
