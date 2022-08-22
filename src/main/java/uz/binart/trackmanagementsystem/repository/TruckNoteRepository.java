package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.TruckNote;

import java.util.List;

@Repository
public interface TruckNoteRepository extends JpaRepository<TruckNote, Long>, JpaSpecificationExecutor<TruckNote> {

    Page<TruckNote> findAllByTruckId(Long truckId, Pageable pageable);

}
