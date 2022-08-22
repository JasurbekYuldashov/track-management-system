package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.binart.trackmanagementsystem.dto.UnitDto;
import uz.binart.trackmanagementsystem.model.Unit;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UnitService {

    Unit create(Unit unit, Long userId);

    List<Unit> findAllDeletedFalse();

    String getUnitTypeName(Long unitId);

    List<Unit> findByLicensePlateExpirationAfter(Long time);

    List<Unit> findByAnnualInspectionExpirationTimeAfter(Long time);

    List<Unit> findByRegistrationExpirationTimeAfter(Long time);

    Boolean existsByNumber(String number);

    Unit getByNumber(String number);

    void deleteByNumber(String number, Long userId);

    Page<Unit> findFiltered(UnitDto unitDto, Pageable pageable);

    List<Unit> findFiltered(UnitDto unitDto, Sort sort);

    Page<Unit> search(Pageable pageable);

    List<Unit> findAllWithExpiredEld(Long waitingForEld);

    List<Unit> findAllByType(Long unitTypeId);

    Boolean existsById(Long id);

    Unit getById(Long id);

    List<Unit> findByNumber(String number, List<Long> visibleIds);

    Optional<Unit> getIfExists(Long id);

    void deleteById(Long id);

    Optional<Unit> findById(Long id);

    Unit update(Unit unit);

    Unit changeStatus(Long unitId, Long statusId, Long userId, Long eldUntil);

    Unit detachTripAndSetReady(Long unitId);

    Map<String, Object> getUnitsLoad(Long start, Long end, List<Long> visibleCompanyIds, List<Long> visibleTeamIds);

}
