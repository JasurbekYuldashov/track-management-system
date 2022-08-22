package uz.binart.trackmanagementsystem.service.type;

import uz.binart.trackmanagementsystem.model.type.CustomerType;

import java.util.List;

public interface CustomerTypeService {

    List<CustomerType> getAll();

    CustomerType getById(Long id);

}
