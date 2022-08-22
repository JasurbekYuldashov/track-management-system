package uz.binart.trackmanagementsystem.repository.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.type.QuantityType;

@Repository
public interface QuantityTypeRepository extends JpaRepository<QuantityType, Long>, JpaSpecificationExecutor<QuantityType> {
}
