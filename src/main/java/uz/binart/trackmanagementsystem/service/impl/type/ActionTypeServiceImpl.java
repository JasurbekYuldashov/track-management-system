package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.ActionType;
import uz.binart.trackmanagementsystem.repository.type.ActionTypeRepository;
import uz.binart.trackmanagementsystem.service.type.ActionTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionTypeServiceImpl implements ActionTypeService {

    private final ActionTypeRepository actionTypeRepository;

    public List<ActionType> getAll(){
        return actionTypeRepository.findAll();
    }

    public Boolean existsById(Long id){
        return actionTypeRepository.existsById(id);
    }

    public ActionType findById(Long id){
        return actionTypeRepository.getOne(id);
    }

}
