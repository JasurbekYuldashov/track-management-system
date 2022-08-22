package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.ChildDto;
import uz.binart.trackmanagementsystem.dto.CompanyDto;
import uz.binart.trackmanagementsystem.model.Company;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.model.type.CustomerType;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.CustomerTypeService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

import static uz.binart.trackmanagementsystem.dto.ResponseData.response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final CustomerTypeService customerTypeService;
    private final StateProvinceService stateProvinceService;
    private final UserService userService;
    private final PackagingService packagingService;
    private final LocationService locationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getContext(){
        Map<String, Object> result = new HashMap<>();

        result.put("customer_types", customerTypeService.getAll());
        result.put("state_province", stateProvinceService.getAll());
        result.put("locations", locationService.findAllFormatted());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/main_table")
    public ResponseEntity<Map<String, Object>> searchMainTable(@RequestParam(name = "q", required = false, defaultValue = "")String searchText, @PageableDefault(page = 0, size = 20) Pageable pageable){
        Map<String, Object> map = new HashMap<>();
        Page<Company> page = companyService.findByNameInput(searchText, pageable);
        map.put("content", packToDto(page.getContent()));
        map.put("page", page.getNumber());
        map.put("totalPages", page.getTotalPages());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CompanyDto>> search(@RequestParam(name = "q", required = false, defaultValue = "")String searchText, @PageableDefault(page = 0, size = 20) Pageable pageable){
        return ResponseEntity.ok(packToDto(companyService.findByNameInput(searchText, pageable).getContent()));
    }

    @PostMapping("/new")
    public ResponseEntity addNewCompany(@RequestBody CompanyDto companyDto){
        Company company = modelMapper.map(companyDto, Company.class);
        Long userId = userService.getCurrentUserFromContext().getId();
        Company savedCompany = companyService.save(company, userId);

        if(savedCompany == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(savedCompany.getId());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewCompany(@RequestBody CompanyDto companyDto){
        Company company = modelMapper.map(companyDto, Company.class);
        Long userId = userService.getCurrentUserFromContext().getId();
        Company savedCompany = companyService.save(company, userId);

        if(savedCompany == null)
            return response("internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

        return response(savedCompany.getId());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getById(@PathVariable @NotNull Long id){
        CompanyDto companyDto = modelMapper.map(companyService.getById(id), CompanyDto.class);
        CustomerType type = customerTypeService.getById(Long.parseLong(companyDto.getCustomerTypeId()));
        if(type != null)
            companyDto.setCustomerType(type.getName());
        else companyDto.setCustomerType("");
        return ResponseEntity.ok(companyDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @NotNull Long id){
        Long userId = userService.getCurrentUserFromContext().getId();
        companyService.deleteById(id, userId);
        return ResponseEntity.ok("company with " + id + " id was successfully deleted");
    }

    @GetMapping("/list")
    public ResponseEntity<List<CompanyDto>> getList(Pageable pageable){
        return ResponseEntity.ok(packToDto(companyService.findFiltered(pageable).getContent()));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editCompany(@RequestBody @Valid CompanyDto companyDto){

        User user = userService.getCurrentUserFromContext();

        Optional<Company> ownedCompanyOptional = companyService.findById(companyDto.getId());
        Company company;

        if(!ownedCompanyOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no company with such id to update");

        company = ownedCompanyOptional.get();
        Company oldVersion = company;
        company = modelMapper.map(companyDto, Company.class);
        companyService.update(oldVersion, company, user.getId());

        return ResponseEntity.ok().body("updated successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCompany(@RequestBody @Valid CompanyDto companyDto){

        User user = userService.getCurrentUserFromContext();

        Optional<Company> ownedCompanyOptional = companyService.findById(companyDto.getId());
        Company company;

        if(!ownedCompanyOptional.isPresent())
            return response("no company with such id to update", HttpStatus.NOT_FOUND);

        company = ownedCompanyOptional.get();
        Company oldVersion = company;
        company = modelMapper.map(companyDto, Company.class);
        companyService.update(oldVersion, company, user.getId());

        return response("updated successfully");
    }

    @GetMapping("/offices/{id}")
    public ResponseEntity<?> getOffices(@PathVariable @NotNull Long id){

        List<Company> companies = companyService.findOffices(id);

        List<CompanyDto> companyDtoS = new ArrayList<>(companies.size());

        for(Company company: companies){
            companyDtoS.add(modelMapper.map(company, CompanyDto.class));
        }
        Map<String, List<CompanyDto>> data = new HashMap<>();

        data.put("offices", companyDtoS);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/child")
    public ResponseEntity<?> postChild(@RequestBody @Valid ChildDto childDto){

        Company company = companyService.createChild(childDto.getParentId(), childDto.getStreet(), childDto.getCity(), childDto.getStateProvinceId());
        if(company == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to save company");
        Map<String, Object> map = new HashMap<>();
        map.put("id", company.getId());
        map.put("status", "company was created successfully");
        return ResponseEntity.ok(map);
    }

    private List<CompanyDto> packToDto(List<Company> companies){

        List<CompanyDto> dtoS = new ArrayList<>(companies.size());
        for(Company company: companies){
            if(company.getLocationId() == null) continue;
            CompanyDto dto = modelMapper.map(company, CompanyDto.class);
            dto.setCityDto(packagingService.packCityToDto(locationService.findById(company.getLocationId())));
            dtoS.add(dto);
        }
        return dtoS;
    }


}
