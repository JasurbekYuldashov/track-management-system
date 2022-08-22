package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import uz.binart.trackmanagementsystem.model.TruckNote;

public interface TruckNoteService {

    ResponseEntity<?> getAllByTruckIdPageable(Long truckId, Pageable pageable);

    ResponseEntity<?> postNote(String content, Long truckId, Long userId);

    TruckNote save(String content, Long truckId, Long userId);

}
