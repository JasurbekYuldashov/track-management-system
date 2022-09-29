package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.OwnedCompanyListDto;
import uz.binart.trackmanagementsystem.model.OwnedCompany;

import java.util.List;
import java.util.Optional;

public interface OwnedCompanyService {

    List<OwnedCompany> getAll();

    List<OwnedCompanyListDto> findAllForList();

    List<OwnedCompanyListDto> findAllForDashBoard(List<Long> ids);

    List<OwnedCompany> findAllForContext(List<Long> ids);

    String getNameById(Long id);

    Optional<OwnedCompany> findById(Long id);

    OwnedCompany findByAbbreviation(String abbreviation);

    Optional<OwnedCompany> findByAbbreviationOptional(String abbreviation);

    OwnedCompany save(OwnedCompany ownedCompany, Long userId);

    OwnedCompany update(OwnedCompany toUpdate, Long userId);

    void delete(OwnedCompany ownedCompany, Long userId);

    OwnedCompany getFromCache(Long ownedCompanyId);

    List<Long> getIds();

}

