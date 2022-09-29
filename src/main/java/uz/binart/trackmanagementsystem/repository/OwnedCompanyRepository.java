package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.OwnedCompany;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnedCompanyRepository extends JpaRepository<OwnedCompany, Long>, JpaSpecificationExecutor<OwnedCompany> {

    List<OwnedCompany> findAllByDeletedFalseOrDeletedIsNull();

    List<OwnedCompany> findAllByIdInAndDeletedFalseOrDeletedIsNull(List<Long> ids);

    OwnedCompany findByAbbreviation(String abbreviation);

    Optional<OwnedCompany> findFirstByAbbreviation(String abbreviation);

    @Query("select c.abbreviation from OwnedCompany c where id = :id")
    String findAbbreviationViaId(Long id);

    @Query("select c.id from OwnedCompany c")
    List<Long> getIds();

}
