package uz.binart.trackmanagementsystem.repository.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.type.OwnershipType;

@Repository
public interface OwnershipTypeRepository extends JpaRepository<OwnershipType, Long>, JpaSpecificationExecutor<OwnershipType> {
    boolean existsById(Long id);
}
