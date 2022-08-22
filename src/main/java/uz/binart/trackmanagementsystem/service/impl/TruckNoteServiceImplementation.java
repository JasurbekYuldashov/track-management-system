package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.TruckNoteDto;
import uz.binart.trackmanagementsystem.model.TruckNote;
import uz.binart.trackmanagementsystem.model.Unit;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.repository.TruckNoteRepository;
import uz.binart.trackmanagementsystem.repository.UnitRepository;
import uz.binart.trackmanagementsystem.service.TruckNoteService;
import uz.binart.trackmanagementsystem.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uz.binart.trackmanagementsystem.dto.ResponseData.response;
import static uz.binart.trackmanagementsystem.dto.ResponseData.responseBadRequest;

@Service
@RequiredArgsConstructor
public class TruckNoteServiceImplementation implements TruckNoteService {

    private final TruckNoteRepository truckNoteRepository;
    private final UserService userService;
    private final UnitRepository unitRepository;

    public ResponseEntity<?> postNote(String content, Long truckId, Long userId){

        TruckNote savedNote = save(content, truckId, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("message", "created with " + savedNote.getId() + " id");

        return response(map);

    }

    public TruckNote save(String content, Long truckId, Long userId){
        if(!unitRepository.existsById(truckId)){
            return null;
        }
        content = StringUtils.trimToEmpty(content);

        Unit unit = unitRepository.getOne(truckId);

        unit.setNotes(content);
        unitRepository.save(unit);

        TruckNote truckNote = new TruckNote();
        truckNote.setTruckId(truckId);
        truckNote.setAuthorId(userId);
        truckNote.setContent(content);
        truckNote.setPostedDate(System.currentTimeMillis());

        return truckNoteRepository.save(truckNote);
    }

    public ResponseEntity<?> getAllByTruckIdPageable(Long truckId, Pageable pageable){

        Page<TruckNote> notes = truckNoteRepository.findAllByTruckId(truckId, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", notes.getTotalPages());
        map.put("totalElements", notes.getTotalElements());
        map.put("currentPage", notes.getNumber());
        List<TruckNoteDto> notesList = notes.stream().map(this::mapToTruckNoteDto).collect(Collectors.toList());
        map.put("notes", notesList);

        return response(map);
    }

    public TruckNoteDto mapToTruckNoteDto(TruckNote truckNote){
        TruckNoteDto truckNoteDto = new TruckNoteDto();
        Optional<User> userOptional = userService.findById(truckNote.getAuthorId());

        truckNoteDto.setId(truckNote.getId());
        String author = "";
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            if(user.getName() != null && !user.getName().isBlank()){
                author = user.getName();
            }else{
                author = user.getUsername();
            }

        }
        truckNoteDto.setAuthor(author);
        truckNoteDto.setContent(truckNote.getContent());
        truckNoteDto.setPostedDate(truckNote.getPostedDate());

        return truckNoteDto;
    }



}
