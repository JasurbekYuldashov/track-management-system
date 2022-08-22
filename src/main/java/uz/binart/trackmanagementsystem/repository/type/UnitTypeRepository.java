package uz.binart.trackmanagementsystem.repository.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.type.UnitType;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Long>, JpaSpecificationExecutor<UnitType> {

    boolean existsById(Long id);

}
