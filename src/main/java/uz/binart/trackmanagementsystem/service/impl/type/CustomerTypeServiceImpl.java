package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.CustomerType;
import uz.binart.trackmanagementsystem.repository.type.CustomerTypeRepository;
import uz.binart.trackmanagementsystem.service.type.CustomerTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerTypeServiceImpl implements CustomerTypeService {

    private final CustomerTypeRepository customerTypeRepository;

    public List<CustomerType> getAll(){
        return customerTypeRepository.findAll();
    }

    public CustomerType getById(Long id){
        if(customerTypeRepository.existsById(id)){
            return customerTypeRepository.getOne(id);
        }
        else return null;
    }

}
