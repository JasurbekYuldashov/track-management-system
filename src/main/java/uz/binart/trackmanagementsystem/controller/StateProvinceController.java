package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.binart.trackmanagementsystem.model.StateProvince;
import uz.binart.trackmanagementsystem.service.StateProvinceService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/state_province")
public class StateProvinceController {

    private final StateProvinceService stateProvinceService;

    @GetMapping("/all")
    public ResponseEntity<List<StateProvince>> getAll(){
        return ResponseEntity.ok(stateProvinceService.getAll());
    }


}
