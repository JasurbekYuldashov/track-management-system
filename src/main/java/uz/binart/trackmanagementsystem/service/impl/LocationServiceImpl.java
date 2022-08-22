package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.LocationListDto;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.repository.LocationRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.LocationService;
import uz.binart.trackmanagementsystem.service.SequenceService;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;
    private SequenceService sequenceService;
    private ActionService actionService;
    private Map<String, Location> locationMap;


    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, SequenceService sequenceService, ActionService actionService){
        this.locationRepository = locationRepository;
        this.sequenceService = sequenceService;
        this.actionService = actionService;
        initMap();
    }

    private void initMap(){
        locationMap = new HashMap<>();
        List<Location> allUs = findAllByParentAnsi("US");

        for(Location location: allUs){
            locationMap.put(location.getAnsi(), location);
        }
    }

    public Location save(Location location, Long userId){

        if(location.getId() != null)
            throw new IllegalChangeAttemptException("Illegal location change attempt");

        if(location.getAnsi() != null && location.getParentAnsi() != null)
        if(location.getAnsi().toLowerCase().equals(location.getParentAnsi().toLowerCase()))
            throw new WrongEntityStructureException("location cannot be parent to itself");

        sequenceService.updateSequence("locations");
        location.setName(location.getName().toUpperCase());
        Location savedLocation = locationRepository.save(location);
        initMap();
        actionService.captureCreate(savedLocation, "locations", userId);
        return savedLocation;
    }

    public Location save(Location location) {
        initMap();
        return locationRepository.save(location);
    }


    public List<Location> getAll(){
        return locationRepository.findAll();
    }

    public List<LocationListDto> findAllFormatted(){
        List<Location> allLocations = locationRepository.findAll();

        return allLocations.stream().map(location -> new LocationListDto(
                location.getId(),
                location.getAnsi() != null ? location.getAnsi(): "",
                location.getName(), location.getParentAnsi() != null ? location.getParentAnsi(): "",
                location.getParentAnsi() != null ? location.getParentAnsi(): "",
                location.getFirstTimeZone() != null ? location.getFirstTimeZone().toString() : "",
                location.getSecondTimeZone() != null ? location.getSecondTimeZone().toString(): "")
                )
                .collect(Collectors.toList()
                );
    }

    public Location findById(Long id){
        return locationRepository.getOne(id);
    }

    public Location findByAnsi(String ansi){
        return locationRepository.findByAnsiAndParentAnsi(ansi, "US");
    }

    public Location findByAnsiFromCache(String ansi){
        return locationMap.get(ansi);
    }

    public List<Location> findAllByParentAnsi(@NotNull String parentAnsi){
        List<Location> locationsByParentAnsi = locationRepository.findAllByParentAnsiOrderByIdAsc(parentAnsi);
        if(locationsByParentAnsi.isEmpty())
            throw new NotFoundException("no locations with " + parentAnsi + " parent_ansi");
        return locationsByParentAnsi;
    }

    public Page<Location> findCities(String query, Pageable pageable){
        return locationRepository.findAll(citiesFilteringSpecification(query), pageable);
    }

    public List<Location> findAllByName(String query){
        return locationRepository.findAll(citiesFilteringSpecification(query));
    }

    public List<Location> findAllByName(String query, String parentAnsi){
        return locationRepository.findAll(citiesFilteringSpecification(query, parentAnsi));
    }

    public Integer getParentTimeZone(Long locationId){
        Location location = locationRepository.getOne(locationId);
        Location parentLocation = locationRepository.findByAnsiAndParentAnsi(location.getParentAnsi(), "US");
        if(location.getParentTimeZone().equals(1))
            return parentLocation.getFirstTimeZone();
        else return parentLocation.getSecondTimeZone();
    }

    public Specification<Location> citiesFilteringSpecification(String query){
        return ((root, criteriaQuery, criteriaBuilder) -> {

            Predicate queryPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + query.toLowerCase() + "%");
            Predicate selfAnsiIsNullPredicate = criteriaBuilder.isNull(root.get("ansi"));

            return criteriaBuilder.and(queryPredicate, selfAnsiIsNullPredicate);
        });
    }

    public Specification<Location> citiesFilteringSpecification(String query, String parentAnsi){
        return ((root, criteriaQuery, criteriaBuilder) -> {

            Predicate queryPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + query.toLowerCase() + "%");
            Predicate parentAnsiPredicate = criteriaBuilder.like(root.get("parentAnsi"), "%" + parentAnsi + "%");

            return criteriaBuilder.and(queryPredicate, parentAnsiPredicate);
        });
    }



}
