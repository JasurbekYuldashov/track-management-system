package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.TripDto;
import uz.binart.trackmanagementsystem.dto.TripForm;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TripService {

    Trip save(Trip trip, Long userId);

    Trip save(Trip trip);

    Optional<Trip> getById(Long id);

    void deleteById(Long id, Long userId);

    Page<Trip> findFiltered(Pageable pageable);

    Page<Trip> findFiltered2(TripDto tripDto, Pageable pageable);

    Page<Trip> findFiltered(Long userId, Pageable pageable);

    void update(Trip trip);

    void updateTrip(TripForm tripForm);

    Page<Trip> getByTruckIdLast(Long id);

    Load getTripsLoad(Trip trip);
    ArrayList<Long> getTruckLoadsIds(Long tripId);

}
