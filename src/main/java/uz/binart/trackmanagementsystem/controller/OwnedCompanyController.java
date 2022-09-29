package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.OwnedCompanyDto;
import uz.binart.trackmanagementsystem.dto.OwnedCompanyListDto;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.OwnedCompany;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.*;

import javax.validation.constraints.NotNull;
import java.util.*;

import static uz.binart.trackmanagementsystem.dto.ResponseData.response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owned_company")
public class OwnedCompanyController {

    private final OwnedCompanyService ownedCompanyService;
    private final StateProvinceService stateProvinceService;
    private final FileService fileService;
    private final UserService userService;
    private final UtilService utilService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getContext(){

        Map<String, Object> result = new HashMap<>();

        result.put("state_province", stateProvinceService.getAll());

        return ResponseEntity.ok(result);

    }

    @GetMapping("/all")
    public ResponseEntity<List<OwnedCompanyListDto>> getAll(){
        return ResponseEntity.ok(ownedCompanyService.findAllForList());
    }

    @GetMapping("/all_by_visibility")
    public ResponseEntity<List<OwnedCompanyListDto>> getAllByVisibility(){
        User user = userService.getCurrentUserFromContext();
        List<Long> visibleIds = utilService.getVisibleIds(user);
        return ResponseEntity.ok(ownedCompanyService.findAllForDashBoard(visibleIds));
    }

    @GetMapping("/all_for_dashboard")
    public ResponseEntity<?> getAllForDashBoard(){
        User user = userService.getCurrentUserFromContext();
        if(user == null)
            return ResponseEntity.badRequest().body("Bad request");

        List<Long> visibleIds = user.getVisibleIds();
        if(visibleIds == null)
            return getAll();

        return ResponseEntity.ok(ownedCompanyService.findAllForDashBoard(visibleIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable @NotNull Long id){

        Optional<OwnedCompany> ownedCompany = ownedCompanyService.findById(id);

        if(!ownedCompany.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldn't find owned company with " + id + " id number");

        OwnedCompanyDto ownedCompanyDto = modelMapper.map(ownedCompany.get(), OwnedCompanyDto.class);

        return ResponseEntity.ok().body(ownedCompanyDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable @NotNull Long id){
        Optional<OwnedCompany> ownedCompany = ownedCompanyService.findById(id);

        if(!ownedCompany.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldn't find owned company with " + id + " id number");

        User user = userService.getCurrentUserFromContext();

        ownedCompanyService.delete(ownedCompany.get(), user.getId());

        return ResponseEntity.ok("deleted successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCompany(@RequestBody OwnedCompanyDto ownedCompanyDto){
        OwnedCompany ownedCompany = modelMapper.map(ownedCompanyDto, OwnedCompany.class);
        User user = userService.getCurrentUserFromContext();
        OwnedCompany saved = ownedCompanyService.save(ownedCompany, user.getId());
        return ResponseEntity.ok().body(saved);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editCompany(@RequestBody OwnedCompanyDto ownedCompanyDto){

        Optional<OwnedCompany> ownedCompanyOptional = ownedCompanyService.findById(ownedCompanyDto.getId());
        OwnedCompany ownedCompany;

        if(!ownedCompanyOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no company with such id to update");

        User user = userService.getCurrentUserFromContext();

        ownedCompany = ownedCompanyOptional.get();
        if(ownedCompany.getFiles() != null) {
            Map<Long, String> files = ownedCompany.getFiles();
            Set<Long> filesKeySet = files.keySet();
            Map<Long, String> newFiles = ownedCompanyDto.getFiles();
            Set<Long> newFilesKeySet = newFiles.keySet();
            for(Long oldFileId: filesKeySet){
                if(!newFilesKeySet.contains(oldFileId))
                    fileService.deleteById(oldFileId, user.getId());
            }
        }

        if(ownedCompanyDto.getLogoFileId() == null || ownedCompany.getLogoFileId()  != null && !ownedCompanyDto.getLogoFileId() .equals(ownedCompany.getLogoFileId()))
            fileService.deleteById(ownedCompany.getLogoFileId(), user.getId());

        ownedCompany = modelMapper.map(ownedCompanyDto, OwnedCompany.class);

        ownedCompanyService.update(ownedCompany, user.getId());
        return ResponseEntity.ok().body("updated successfully");
    }

}
