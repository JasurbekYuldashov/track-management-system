package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.LocationListDto;
import uz.binart.trackmanagementsystem.model.Location;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface LocationService {

    Location save(Location location, Long userId);

    Location save(Location location);

    List<Location> getAll();

    List<LocationListDto> findAllFormatted();

    Location findById(Long id);

    Location findByAnsi(String ansi);

    Location findByAnsiFromCache(String ansi);

    List<Location> findAllByParentAnsi(@NotNull String parentAnsi);

    Page<Location> findCities(String query, Pageable pageable);

    List<Location> findAllByName(String query);

    List<Location> findAllByName(String query, String parentAnsi);

    Integer getParentTimeZone(Long locationId);

}
