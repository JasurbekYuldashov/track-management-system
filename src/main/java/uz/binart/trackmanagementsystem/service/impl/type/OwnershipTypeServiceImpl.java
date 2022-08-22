package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.OwnershipType;
import uz.binart.trackmanagementsystem.repository.type.OwnershipTypeRepository;
import uz.binart.trackmanagementsystem.service.type.OwnershipTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnershipTypeServiceImpl implements OwnershipTypeService {

    private final OwnershipTypeRepository ownershipTypeRepository;

    public Boolean existsById(Long id){
        return ownershipTypeRepository.existsById(id);
    }

    public OwnershipType getOne(Long id){
        return ownershipTypeRepository.getOne(id);
    }

    public List<OwnershipType> getAll(){
        return ownershipTypeRepository.findAll();
    }

}
