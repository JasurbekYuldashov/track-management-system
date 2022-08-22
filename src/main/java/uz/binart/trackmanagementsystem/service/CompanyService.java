package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    Company save(Company company, Long userId);

    Company save(Company company);

    Company getById(Long id);

    Optional<Company> findById(Long id);

    void deleteById(Long id, Long userId);

    Page<Company> findFiltered(Pageable pageable);

    Page<Company> findByNameInput(String searchText, Pageable pageable);

    Company update(Company oldVersion, Company company, Long userId);

    List<Company> findOffices(Long id);

    List<Company> getPossibleParents();

    Company createChild(Long parentId, String street, String city, Long stateProvinceId);

    List<Company> findAll();

}
