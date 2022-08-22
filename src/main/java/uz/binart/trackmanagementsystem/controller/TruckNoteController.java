package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.NewNoteDto;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.TruckNoteService;
import uz.binart.trackmanagementsystem.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/truck_notes")
public class TruckNoteController {

    private final TruckNoteService truckNoteService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getByTruckId(@RequestParam(name = "truck_id")Long truckId, @PageableDefault(size = 30, sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable){
        return truckNoteService.getAllByTruckIdPageable(truckId, pageable);
    }

    @PostMapping
    public ResponseEntity<?> postNote(@RequestBody NewNoteDto newNoteDto){

        User user = userService.getCurrentUserFromContext();

        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        return truckNoteService.postNote(newNoteDto.getContent(), newNoteDto.getTruckId(), user.getId());

    }

}
