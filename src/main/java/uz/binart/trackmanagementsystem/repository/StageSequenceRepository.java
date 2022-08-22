package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.StageSequence;

@Repository
public interface StageSequenceRepository extends JpaRepository<StageSequence, Long>, JpaSpecificationExecutor<StageSequence> {

}
