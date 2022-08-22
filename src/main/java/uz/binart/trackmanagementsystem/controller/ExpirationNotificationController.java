package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.binart.trackmanagementsystem.model.ExpirationNotification;
import uz.binart.trackmanagementsystem.service.ExpirationNotificationService;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expiration_notification")
public class ExpirationNotificationController {

    private final ExpirationNotificationService expirationNotificationService;

    @GetMapping("/actual")
    public ResponseEntity<?> getActualNotification(){
        return ResponseEntity.ok(expirationNotificationService.getActualNotifications());
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOfNotifications(@PageableDefault(size = 20, sort = {"id"}, direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(expirationNotificationService.getAsList(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotification(@PathVariable @NotNull Long id){
        ExpirationNotification expirationNotification = expirationNotificationService.getById(id);
        if(expirationNotification == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no notification with such id");
        }
        return ResponseEntity.ok(expirationNotification);
    }

}
