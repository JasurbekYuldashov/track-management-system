package uz.binart.trackmanagementsystem.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.binart.trackmanagementsystem.dto.AccountingDto;
import uz.binart.trackmanagementsystem.dto.MobileDto;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounting")
@RequiredArgsConstructor
public class AccountingController {

    private final UserService userService;
    private final AccountingService accountingService;

    @GetMapping("/info")
    public ResponseEntity<?> getForInfo(@RequestParam(name = "carrier", required = false)Long carrierId,
                                        @RequestParam(name = "truck_number", required = false)Long truckId,
                                        @RequestParam(name = "driver_id", required = false)Long driverId,
                                        @RequestParam(name = "team_id", required = false)Long teamId,
                                        @RequestParam(name = "all_by_companys_truck", required = false)Long allByCompanysTruckId,
                                        @RequestParam(name = "startTime")Long startTime,
                                        @RequestParam(name = "endTime")Long endTime){

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(new Date(startTime));
        end.setTime(new Date(endTime));
        User user = userService.getCurrentUserFromContext();

        if(user == null){
            MobileDto mobileDto = new MobileDto();
            mobileDto.setErrorMessage("don't have permission");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getRoleId().equals(2))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you don't have permission");
        List<AccountingDto> list = accountingService.getProperInfo(carrierId, truckId, driverId, teamId, allByCompanysTruckId, startTime, endTime, false, user.getRoleId() == 2);
        return ResponseEntity.ok(list);
    }


    @GetMapping
    public ResponseEntity<?> getAccountingInfo(@RequestParam(name = "carrier", required = false)Long carrierId,
                                               @RequestParam(name = "truck_number", required = false)Long truckId,
                                               @RequestParam(name = "driver_id", required = false)Long driverId,
                                               @RequestParam(name = "team_id", required = false)Long teamId,
                                               @RequestParam(name = "all_by_companys_truck", required = false)Long allByCompanysTruckId,
                                               @RequestParam(name = "startTime")Long startTime,
                                               @RequestParam(name = "endTime")Long endTime,
                                               @RequestParam(name = "weekly", required = false, defaultValue = "false")Boolean weekly) {

        User user = userService.getCurrentUserFromContext();

        if(user == null){
            MobileDto mobileDto = new MobileDto();
            mobileDto.setErrorMessage("don't have permission");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mobileDto);
        }

        if(user.getRoleId().equals(2))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you don't have permission");

        List<AccountingDto> list = accountingService.getProperInfo(carrierId, truckId, driverId, teamId, allByCompanysTruckId, startTime, endTime, weekly, user.getRoleId() == 2);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header("Content-Disposition", "attachment; filename=report.xlsx")
                .body(new ByteArrayResource(ExcelFileExporter.accountingInfoDto(list, startTime, endTime, weekly, user.getRoleId() == 2).readAllBytes()));

    }


}
