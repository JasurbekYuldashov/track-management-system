package uz.binart.trackmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.Unit;

import java.util.List;
import java.util.Set;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {

    Boolean existsByNumber(String number);

    Unit  findByNumber(String number);

    void deleteByNumber(String number);

    List<Unit> findAllByUnitTypeId(Long unitTypeId);

    List<Unit> findAllByDeletedFalse();

    @Query("select u from Unit u where u.eldUnTil < :currentTime")
    List<Unit> findAllWithExpiredEld(Long currentTime);

    List<Unit> findAllByLicensePlateExpirationTimeGreaterThanAndNotifiedOfLicensePlateExpiration(Long licencePlateExpiration, Boolean notifiedOfLicense);

    List<Unit> findAllByAnnualInspectionExpirationTimeGreaterThanAndNotifiedOfInspection(Long annualInspectionExpirationTime, Boolean notifiedOfInspection);

    List<Unit> findAllByRegistrationExpirationTimeGreaterThanAndNotifiedOfRegistration(Long registrationExpirationTime, Boolean notifiedOfRegistration);

    @Query("select u.id from Unit u where u.teamId = :teamId")
    Set<Long> findRequiredViaTeamId(Long teamId);

    @Query("select u.id from Unit u where lower(u.number) like :number")
    Set<Long> getIdsByNumber(String number);

}
