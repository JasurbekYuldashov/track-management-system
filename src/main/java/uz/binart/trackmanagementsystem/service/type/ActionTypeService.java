package uz.binart.trackmanagementsystem.service.type;


import uz.binart.trackmanagementsystem.model.type.ActionType;

import java.util.List;

public interface ActionTypeService {

    List<ActionType> getAll();

    Boolean existsById(Long id);

    ActionType findById(Long id);

}
