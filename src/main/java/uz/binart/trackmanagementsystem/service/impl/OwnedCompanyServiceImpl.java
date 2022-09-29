package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.OwnedCompanyListDto;
import uz.binart.trackmanagementsystem.exception.FieldLimitException;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.mapper.OwnedCompanyMapper;
import uz.binart.trackmanagementsystem.model.OwnedCompany;
import uz.binart.trackmanagementsystem.model.StateProvince;
import uz.binart.trackmanagementsystem.repository.OwnedCompanyRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.OwnedCompanyService;
import uz.binart.trackmanagementsystem.service.SequenceService;
import uz.binart.trackmanagementsystem.service.StateProvinceService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnedCompanyServiceImpl implements OwnedCompanyService {

    private final OwnedCompanyRepository ownedCompanyRepository;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final StateProvinceService stateProvinceService;
    private Map<Long, OwnedCompany> cache;

    private OwnedCompanyMapper ownedCompanyMapper;

    @PostConstruct
    public void initCache(){
        cache = new HashMap<>();
        List<OwnedCompany> ownedCompanies = getAll();

        for(OwnedCompany ownedCompany: ownedCompanies){
            cache.put(ownedCompany.getId(), ownedCompany);
        }

    }

    public List<OwnedCompany> getAll(){
        return ownedCompanyRepository.findAllByDeletedFalseOrDeletedIsNull();
    }

    public List<OwnedCompanyListDto> findAllForList(){
        return ownedCompanyRepository.findAllByDeletedFalseOrDeletedIsNull().stream().map(ownedCompany -> {
            StateProvince province = stateProvinceService.getById(ownedCompany.getStateProvinceId());
            return new OwnedCompanyListDto(ownedCompany.getId(), ownedCompany.getName(), ownedCompany.getAbbreviation(), province.getName(), ownedCompany.getCity());
        }).collect(Collectors.toList());
    }

    public List<OwnedCompanyListDto> findAllForDashBoard(List<Long> ids){
        return ownedCompanyRepository.findAllByIdInAndDeletedFalseOrDeletedIsNull(ids).stream().map(ownedCompany -> {
            StateProvince province = stateProvinceService.getById(ownedCompany.getStateProvinceId());
            return new OwnedCompanyListDto(ownedCompany.getId(), ownedCompany.getName(), ownedCompany.getAbbreviation(), province.getName(), ownedCompany.getCity());
        }).collect(Collectors.toList());
    }

    public List<OwnedCompany> findAllForContext(List<Long> ids){
        return ownedCompanyRepository.findAllById(ids);
    }

    public String getNameById(Long id){
        if(ownedCompanyRepository.existsById(id)){
            OwnedCompany company = ownedCompanyRepository.getOne(id);
            return company.getName();
        }
        else
            return "";
    }

    public Optional<OwnedCompany> findById(Long id){
        return ownedCompanyRepository.findById(id);
    }

    public OwnedCompany findByAbbreviation(String abbreviation){
        return ownedCompanyRepository.findByAbbreviation(abbreviation);
    }

    public Optional<OwnedCompany> findByAbbreviationOptional(String abbreviation){
        return ownedCompanyRepository.findFirstByAbbreviation(abbreviation);
    }

    public OwnedCompany save(OwnedCompany ownedCompany, Long userId){

        if(ownedCompany.getId() != null)
            throw new IllegalChangeAttemptException("owned company with such id already exists");

        if(ownedCompany.getFiles() != null && ownedCompany.getFiles().size() > 10)
            throw new FieldLimitException("cannot save more than 10 files for company");

        sequenceService.updateSequence("owned_companies");
        OwnedCompany savedCompany = ownedCompanyRepository.save(ownedCompany);
        initCache();
        actionService.captureCreate(savedCompany, "owned_companies", userId);
        return savedCompany;
    }

    public OwnedCompany update(OwnedCompany ownedCompany, Long userId){
        if(ownedCompany.getFiles() != null && ownedCompany.getFiles().size() > 10)
            throw new FieldLimitException("cannot save more than 10 files for company");

        OwnedCompany oldCompany = ownedCompanyRepository.getOne(ownedCompany.getId());
        actionService.captureUpdate(oldCompany, ownedCompany, "owned_companies", userId);
        initCache();
        return ownedCompanyRepository.save(ownedCompany);
    }

    public void delete(OwnedCompany ownedCompany, Long userId){
        ownedCompany.setDeleted(true);
        initCache();
        ownedCompanyRepository.save(ownedCompany);
    }

    public OwnedCompany getFromCache(Long ownedCompanyId){
        return cache.get(ownedCompanyId);
    }

    public List<Long> getIds(){
        return ownedCompanyRepository.getIds();
    }

}
