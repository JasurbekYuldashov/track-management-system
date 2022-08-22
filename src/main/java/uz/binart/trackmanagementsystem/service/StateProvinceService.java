package uz.binart.trackmanagementsystem.service;

import uz.binart.trackmanagementsystem.model.StateProvince;

import java.util.List;

public interface StateProvinceService {

    List<StateProvince> getAll();

    StateProvince getById(Long id);

    Boolean existsById(Long id);

    List<StateProvince> findByName(String stateProvinceName);

}
