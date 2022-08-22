package uz.binart.trackmanagementsystem.service.type;

import uz.binart.trackmanagementsystem.model.type.OwnershipType;

import java.util.List;

public interface OwnershipTypeService {

    Boolean existsById(Long id);

    OwnershipType getOne(Long id);

    List<OwnershipType> getAll();

}
