package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.ActionListDto;
import uz.binart.trackmanagementsystem.dto.NewUserDto;
import uz.binart.trackmanagementsystem.dto.UserDto;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.Action;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.ActionTypeService;

import javax.validation.constraints.NotNull;
import java.util.*;

import static uz.binart.trackmanagementsystem.dto.ResponseData.response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ActionService actionService;
    private final ActionTypeService actionTypeService;
    private final OwnedCompanyService ownedCompanyService;
    private final UtilService utilService;
    private final TeamService teamService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/user_context")
    public ResponseEntity<?> getNewUserContext(){

        Map<String, Object> map = new HashMap<>();
        map.put("companies", ownedCompanyService.getAll());
        map.put("teams", teamService.findAll());

        return response(map);
    }

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody NewUserDto newUserDto){

        newUserDto.setUsername(StringUtils.trimToNull(newUserDto.getUsername()));
        newUserDto.setPassword(StringUtils.trimToNull(newUserDto.getPassword()));

        User currentUser = userService.getCurrentUserFromContext();

        if(newUserDto.getUsername() == null || newUserDto.getPassword() == null)
            return ResponseEntity.badRequest().body("invalid username or/and password");

        User user = userService.createNewUser(newUserDto, currentUser.getId());

        if(user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("couldn't save user by " + newUserDto + " payload");

        return ResponseEntity.ok().body("new user was successfully created");
    }

    @PutMapping("/edit_user")
    public ResponseEntity<?> updateUser(@RequestBody NewUserDto newUserDto){

        newUserDto.setUsername(StringUtils.trimToNull(newUserDto.getUsername()));
        newUserDto.setPassword(StringUtils.trimToNull(newUserDto.getPassword()));
        User currentUser = userService.getCurrentUserFromContext();
        Long userId = currentUser.getId();

        User user = userService.updateUser(newUserDto, userId);

        if(user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("couldn't update user by " + newUserDto + " payload");
        return ResponseEntity.ok().body("new user was successfully created");
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(name = "id", required = false)Long id,
                                                        @RequestParam(name = "username", required = false)String username,
                                                        @RequestParam(name = "phone", required = false)String phone,
                                                        Pageable pageable){

        username = StringUtils.trimToNull(username);
        phone = StringUtils.trimToNull(phone);

        Page<User> dispatchers = userService.findFiltered(id, username, null, phone, pageable);
        Map<String, Object> map = new HashMap<>();
        List<UserDto> dtoS = new ArrayList<>(dispatchers.getSize());

        for(User dispatcher: dispatchers){
            UserDto userDto = modelMapper.map(dispatcher, UserDto.class);
            userDto.setRole(defineRole(dispatcher.getRoleId()));
            dtoS.add(userDto);
        }

        map.put("pageNumber", dispatchers.getNumber());
        map.put("totalElements", dispatchers.getTotalElements());
        map.put("totalPages", dispatchers.getTotalPages());
        map.put("content", dtoS);
        map.put("sort", dispatchers.getSort());

        return ResponseEntity.ok().body(map);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @NotNull Long id){

        User user = userService.getCurrentUserFromContext();

        if(user.getId().equals(id)){
            return ResponseEntity.badRequest().body("cannot delete himself");
        }

        userService.deleteById(id, user.getId());

        return ResponseEntity.ok().body("user was deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable @NotNull Long id){
        Optional<User> userOptional = userService.findById(id);

        if(!userOptional.isPresent())
            throw new NotFoundException();

        User user = userOptional.get();
        UserDto userDto = modelMapper.map(user, UserDto.class);

        if(userDto.getVisibleIds() == null || userDto.getVisibleIds().isEmpty())
            userDto.setVisibleIds(utilService.getVisibleIds(user));

        if(userDto.getVisibleTeamIds() == null || userDto.getVisibleIds().isEmpty())
            userDto.setVisibleTeamIds(utilService.getVisibleTeamIds(user));

        userDto.setRoleName(defineRole(user.getRoleId()));
        userDto.setAvailableCompanies(ownedCompanyService.getAll());
        userDto.setAvailableTeams(teamService.findAll());

        return ResponseEntity.ok().body(userDto);
    }

    @GetMapping("/log")
    public ResponseEntity<Map<String, Object>> getLogByUserId(Pageable pageable){

        Page<Action> actions = actionService.findFiltered(pageable);

        Map<String, Object> res = new HashMap<>();
        List<ActionListDto> actionDtoList = new ArrayList<>();
        for(Action action: actions){
            ActionListDto actionListDto = new ActionListDto();
            if(action.getMadeById() != null) {
                Optional<User> userOpt = userService.findById(action.getMadeById());
                if (userOpt.isPresent()) {
                    actionListDto.setUsername(userOpt.get().getUsername());
                }
            }
            actionListDto.setTableName(action.getTableName());
            actionListDto.setActionType(actionTypeService.findById(action.getActionTypeId()).getName());
            actionListDto.setTimeStamp(action.getActionTime().getTime());
            Map<String, Object> map = modelMapper.map(action.getInitialObject(), HashMap.class);

            actionListDto.setEntityId(Long.parseLong(map.get("id").toString()));
            actionDtoList.add(actionListDto);
        }
        res.put("pageNumber", actions.getNumber());
        res.put("size", actions.getSize());
        res.put("totalPages", actions.getTotalPages());
        res.put("totalElements", actions.getTotalElements());
        res.put("content", actionDtoList);

        return ResponseEntity.ok(res);
    }


    private String defineRole(Integer roleId){
        if(roleId.equals(1))
            return "admin";
        else if(roleId.equals(2))
            return "updater";
        else if(roleId.equals(3))
            return "dispatcher";
        else return "";
    }

}
