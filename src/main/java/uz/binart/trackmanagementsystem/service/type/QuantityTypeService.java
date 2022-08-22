package uz.binart.trackmanagementsystem.service.type;

import uz.binart.trackmanagementsystem.model.type.QuantityType;

import java.util.List;

public interface QuantityTypeService {

    List<QuantityType> getAll();

    Boolean existsById(Long id);

}
