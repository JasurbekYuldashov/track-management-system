package uz.binart.trackmanagementsystem.service.type;


import uz.binart.trackmanagementsystem.model.type.UnitType;

import java.util.List;

public interface UnitTypeService {

    Boolean existsById(Long id);

    UnitType getFromManualCache(Long id);

    List<UnitType> getAll();

    UnitType getById(Long id);

}
