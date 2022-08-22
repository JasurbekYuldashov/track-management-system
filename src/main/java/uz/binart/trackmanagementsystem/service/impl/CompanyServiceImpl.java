package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.repository.CompanyRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.CompanyService;
import uz.binart.trackmanagementsystem.service.SequenceService;
import uz.binart.trackmanagementsystem.service.StateProvinceService;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final StateProvinceService stateProvinceService;

    public Company save(Company company, Long userId){
        if(company.getId() != null && companyRepository.existsById(company.getId())){
            log.warn("illegal change attempt to companies table");
            throw new IllegalChangeAttemptException();
        }
        sequenceService.updateSequence("companies");
        company.setCompanyName(company.getCompanyName().toUpperCase());
        Company newCompany = companyRepository.save(company);
        actionService.captureCreate(newCompany, "companies", userId);
        return newCompany;
    }

    public Company save(Company company){
        return companyRepository.save(company);
    }

    public Company getById(Long id){
        if(!companyRepository.existsById(id)){
            log.warn("Not found in " + this.getClass() + " service");
            throw new NotFoundException();
        }
        return companyRepository.getOne(id);
    }

    public Optional<Company> findById(Long id){
        return companyRepository.findById(id);
    }

    public void deleteById(Long id, Long userId){
        if(!companyRepository.existsById(id)){
            log.warn("unable to delete with such id: " + id + " from companies table");
            throw new NotFoundException();
        }
        Company deletingCompany = companyRepository.getOne(id);
        deletingCompany.setDeleted(true);
        actionService.captureDelete(deletingCompany, "companies", userId);
        companyRepository.save(deletingCompany);
    }

    public Page<Company> findFiltered(Pageable pageable){
        return companyRepository.findAllByDeletedFalseOrDeletedIsNull(pageable);
    }

    public Page<Company> findByNameInput(String searchText, Pageable pageable){
        return companyRepository.findAll(searchTextSpecification(searchText), pageable);
    }

    public Company update(Company oldVersion, Company company, Long userId) {
        company.setCompanyName(company.getCompanyName().toUpperCase());
        Company updatedCompany = companyRepository.save(company);
        actionService.captureUpdate(oldVersion, company, "companies", userId);
        return updatedCompany;
    }

    public List<Company> findOffices(Long id){
        return companyRepository.findAllByIdOrParentIdAndDeletedFalseOrderByIdAsc(id, id);
    }

    public List<Company> getPossibleParents(){
        return companyRepository.findAllByParentIdIsNullOrderByIdAsc();
    }

    public Company createChild(Long parentId, String street, String city, Long stateProvinceId){

        if(!stateProvinceService.existsById(stateProvinceId))
            throw new WrongEntityStructureException("no such state province");

        if(!companyRepository.existsById(parentId))
            throw new WrongEntityStructureException("no such company");

        Company parentCompany = companyRepository.getOne(parentId);
        if(parentCompany.getParentId() != null)
            throw new WrongEntityStructureException("office can not be head of another office");

        Company company = parentCompany;

        company.setId(null);
        company.setParentId(parentId);
        company.setStreet(street);
        company.setStateProvinceId(stateProvinceId);
        company.setCity(city);
        return companyRepository.save(company);
    }

    public List<Company> findAll(){
        return companyRepository.findAllByDeletedFalseOrDeletedIsNull();
    }

    Specification<Company> searchTextSpecification(String searchText){

        return ((root, criteriaQuery, criteriaBuilder) -> {
            String[] splittedByBlankText = searchText.split(" ");

            for (int i = 0; i < splittedByBlankText.length; i++) {
                splittedByBlankText[i] = splittedByBlankText[i].trim();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (String word : splittedByBlankText) {
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), "%" + word.toLowerCase() + "%");
                Predicate cityPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("city")), "%" + word.toUpperCase() + "%");
                Predicate streetPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("street")), "%" + word.toUpperCase() + "%");
                predicates.add(criteriaBuilder.or(namePredicate, cityPredicate, streetPredicate));

                predicates.add(namePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        });

    }

}
