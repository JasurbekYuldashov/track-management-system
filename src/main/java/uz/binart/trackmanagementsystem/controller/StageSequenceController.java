package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.StageSequenceDto;
import uz.binart.trackmanagementsystem.model.StageSequence;
import uz.binart.trackmanagementsystem.service.StageSequenceService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage_sequence")
public class StageSequenceController {

    private final StageSequenceService stageSequenceService;
    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<String> createNewSequence(@RequestBody @Valid StageSequenceDto stageSequenceDto){

        ModelMapper mapper = new ModelMapper();
        StageSequence stageSequence = mapper.map(stageSequenceDto, StageSequence.class);
        Long userId = userService.getCurrentUserFromContext().getId();
        stageSequenceService.create(stageSequence, userId);

        return ResponseEntity.ok("new sequence for trip was successfully created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageSequenceDto> getById(@PathVariable @NotNull Long id){

        StageSequence stageSequence = stageSequenceService.findById(id);
        ModelMapper mapper = new ModelMapper();
        StageSequenceDto stageSequenceDto = mapper.map(stageSequence, StageSequenceDto.class);

        return ResponseEntity.ok(stageSequenceDto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<StageSequence>> getList(@RequestParam(required = false) String name, Pageable pageable){
        name = StringUtils.trimToNull(name);
        return ResponseEntity.ok(stageSequenceService.findFiltered(name, pageable));
    }

}
