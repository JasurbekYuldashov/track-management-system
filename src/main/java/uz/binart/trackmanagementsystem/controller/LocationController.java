package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.CitySearchResultDto;
import uz.binart.trackmanagementsystem.dto.LocationDto;
import uz.binart.trackmanagementsystem.dto.LocationListDto;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.service.LocationService;
import uz.binart.trackmanagementsystem.service.PackagingService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;
    private final PackagingService packagingService;
    private final UserService userService;

    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getContext(){
        Map<String, Object> map = new HashMap<>();

        map.put("states", locationService.findAllByParentAnsi("US"));

        return ResponseEntity.ok(map);
    }

    @PostMapping("/new")
    public ResponseEntity newLocation(@RequestBody LocationDto locationDto){
        Location location = new ModelMapper().map(locationDto, Location.class);
        Long userId = userService.getCurrentUserFromContext().getId();
        return ResponseEntity.ok(locationService.save(location, userId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Location>> getAll(){
        return ResponseEntity.ok(locationService.getAll());
    }

    @GetMapping("/all")
    public ResponseEntity<List<LocationListDto>> getAll2(){
        return ResponseEntity.ok(locationService.findAllFormatted());
    }

    @GetMapping("/{parentAnsi}")
    public ResponseEntity<?> findAllByParentsAnsi(@PathVariable @NotNull String parentAnsi){
        Map<String, Object> answer = new HashMap<>();
        try {
            List<Location> locations = locationService.findAllByParentAnsi(parentAnsi);
            answer.put("error_message", "");
            answer.put("locations", locations);
            return ResponseEntity.ok(answer);
        }catch(Exception exception){
            exception.printStackTrace();
            answer.put("error_message", exception.getMessage());
            return ResponseEntity.badRequest().body(answer);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> findCities(@RequestParam(value = "q", required = false, defaultValue = "")String query, @PageableDefault(page = 0, size = 20) Pageable pageable){

        Map<String, Object> result = new HashMap<>();

        Page<Location> citiesPage = locationService.findCities(query, pageable);
        result.put("page", citiesPage.getNumber());
        result.put("total_elements", citiesPage.getTotalElements());
        result.put("total_pages", citiesPage.getTotalPages());
        result.put("size", citiesPage.getSize());
        result.put("data", packSearchData(citiesPage.getContent()));

        return ResponseEntity.ok(result);
    }

    private List<CitySearchResultDto> packSearchData(List<Location> cities){
        List<CitySearchResultDto> packedData = new ArrayList<>(cities.size());
        for(Location city: cities){
            packedData.add(packagingService.packCityToDto(city));
        }
        return packedData;
    }

    @GetMapping("/by_id/{id}")
    public ResponseEntity<CitySearchResultDto> findById(@PathVariable Long id){
        Location location = locationService.findById(id);
        if(location == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        CitySearchResultDto dto = packagingService.packCityToDto(location);
        return ResponseEntity.ok(dto);
    }

}
