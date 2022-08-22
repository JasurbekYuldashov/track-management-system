package uz.binart.trackmanagementsystem.repository.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.type.DriverType;

@Repository
public interface DriverTypeRepository extends JpaRepository<DriverType, Long>, JpaSpecificationExecutor<DriverType> {
}
