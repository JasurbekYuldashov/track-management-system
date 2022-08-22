package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.StateProvince;
import uz.binart.trackmanagementsystem.repository.StateProvinceRepository;
import uz.binart.trackmanagementsystem.service.StateProvinceService;

import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StateProvinceServiceImpl implements StateProvinceService {

    private final StateProvinceRepository stateProvinceRepository;

    public List<StateProvince> getAll(){
        return stateProvinceRepository.findAll();
    }

    public StateProvince getById(Long id){
        if(stateProvinceRepository.existsById(id))
            return stateProvinceRepository.getOne(id);
        else return null;
    }

    public Boolean existsById(Long id){
        return stateProvinceRepository.existsById(id);
    }

    public List<StateProvince> findByName(String stateProvinceName){
        return stateProvinceRepository.findAll(filteringSpecification(stateProvinceName));
    }

    private Specification<StateProvince> filteringSpecification(String name){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            return namePredicate;
        });
    }

}
