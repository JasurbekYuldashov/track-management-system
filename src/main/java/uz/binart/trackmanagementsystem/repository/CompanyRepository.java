package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    List<Company> findAllByIdOrParentIdAndDeletedFalseOrderByIdAsc(Long Id, Long parentId);

    List<Company> findAllByParentIdIsNullOrderByIdAsc();

    Page<Company> findAllByDeletedFalseOrDeletedIsNull(Pageable pageable);

    List<Company> findAllByDeletedFalseOrDeletedIsNull();

}
