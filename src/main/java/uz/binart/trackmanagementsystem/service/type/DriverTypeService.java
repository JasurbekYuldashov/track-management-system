package uz.binart.trackmanagementsystem.service.type;

import uz.binart.trackmanagementsystem.model.type.DriverType;

import java.util.List;

public interface DriverTypeService {

    DriverType getById(Long id);

    List<DriverType> getAll();

    Boolean existsById(Long id);

}
