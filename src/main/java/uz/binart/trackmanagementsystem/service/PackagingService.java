package uz.binart.trackmanagementsystem.service;

import uz.binart.trackmanagementsystem.dto.CitySearchResultDto;
import uz.binart.trackmanagementsystem.dto.LoadDto;
import uz.binart.trackmanagementsystem.dto.TripDto;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.Location;
import uz.binart.trackmanagementsystem.model.Trip;

public interface PackagingService {

    TripDto packTripToDto(Trip trip);

    LoadDto packLoadToDto(Load load);

    LoadDto loadToDtoSingle(Load load);

    CitySearchResultDto packCityToDto(Location location);

    String getNameAndParentAnsi(Location location);

}
