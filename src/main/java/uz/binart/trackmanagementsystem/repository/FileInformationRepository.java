package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.FileInformation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@Repository
public interface FileInformationRepository extends JpaRepository<FileInformation, Long>, JpaSpecificationExecutor<FileInformation> {
    Optional<FileInformation> findByIdAndDeletedFalse(Long id);
}
