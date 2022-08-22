package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.binart.trackmanagementsystem.service.TruckLoadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/truck_load")
public class TruckLoadController {

    private final TruckLoadService truckLoadService;


    @GetMapping("/table_headers")
    public ResponseEntity<?> getTableHeaders(@RequestParam(name = "start")Long start, @RequestParam(name = "end")Long end){



        return null;
    }





}
