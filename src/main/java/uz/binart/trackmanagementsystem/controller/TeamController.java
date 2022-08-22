package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.TeamPostDto;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.Team;
import uz.binart.trackmanagementsystem.service.TeamService;
import org.springframework.data.domain.Sort;
import uz.binart.trackmanagementsystem.service.UserService;
import uz.binart.trackmanagementsystem.service.UtilService;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UtilService utilService;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try{
             return ResponseEntity.ok(teamService.getById(id));
        }catch (NotFoundException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @GetMapping("/all_by_visibility")
    public ResponseEntity<?> getAllByVisibility(){
        List<Long> visibleTeamIds = utilService.getVisibleTeamIds(userService.getCurrentUserFromContext());
        return ResponseEntity.ok(teamService.getPageByVisibility(visibleTeamIds));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getList(@PageableDefault(page = 0, size = 25, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(teamService.getPage(pageable));
    }

    @PostMapping
    public ResponseEntity<?> addNewTeam(@RequestBody TeamPostDto newTeam){

        if(newTeam.getName() == null || newTeam.getName().isBlank())
            return ResponseEntity.badRequest().body("name should be specified");

        if(newTeam.getColorCode() == null || newTeam.getColorCode().isBlank())
            return ResponseEntity.badRequest().body("color code should be specified");

        Team team = teamService.addNewTeam(newTeam.getName(), newTeam.getColorCode());

        if(team != null){
            return ResponseEntity.ok("team was successfully created");
        }else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("unable to create to new team");
    }


}
