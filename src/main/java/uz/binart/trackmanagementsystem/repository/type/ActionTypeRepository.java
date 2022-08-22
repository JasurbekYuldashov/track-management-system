package uz.binart.trackmanagementsystem.repository.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.type.ActionType;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Long>{

    ActionType getByName(String name);

}
