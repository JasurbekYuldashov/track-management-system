package uz.binart.trackmanagementsystem.service.status;

import uz.binart.trackmanagementsystem.model.status.TripStatus;

import java.util.List;

public interface TripStatusService {

    TripStatus findById(Long id);

    List<TripStatus> getAll();

    Boolean existsById(Long id);

}
