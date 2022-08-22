package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.binart.trackmanagementsystem.dto.CountingDto;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.LoadService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/counting")
@RequiredArgsConstructor
public class CountingController {

    private final LoadService loadService;
    private final UserService userService;

    @GetMapping("/on_load/{loadId}")
    public ResponseEntity<?> getCountingInformation(@PathVariable @NotNull Long loadId){
        User user = userService.getCurrentUserFromContext();
        if(user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("method not allowed");

        if(user.getRoleId() != 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("method not allowed");

        try{
            CountingDto dto = loadService.getCountingInformation(loadId);
            return ResponseEntity.ok(dto);
        }catch (Exception exception){
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/on_load")
    public ResponseEntity<?> postCountingInformation(@RequestBody CountingDto counting){
        User user = userService.getCurrentUserFromContext();
        if(user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("method not allowed");

        if(user.getRoleId() != 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("method not allowed");

        try {

            loadService.setCountingInformation(counting);
            return ResponseEntity.ok().body("counting information was set");

        }catch(Exception exception){

            return ResponseEntity.badRequest().body(exception.getMessage());

        }
    }




}
