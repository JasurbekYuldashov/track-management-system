package uz.binart.trackmanagementsystem.repository.status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.status.UnitStatus;

import java.util.List;
import java.util.Set;

@Repository
public interface UnitStatusRepository extends JpaRepository<UnitStatus, Long>, JpaSpecificationExecutor<UnitStatus> {

    @Query("select us.id from UnitStatus us where us.id not in :statuses")
    Set<Long> getProperStatuses(List<Long> statuses);

}
