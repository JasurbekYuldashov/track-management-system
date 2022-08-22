package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class UnitDashboardDto {

    private Long truckId;

    private Long tripId;

    private String number;

    private Long driverOneId;

    private String driverOne;

    private String driverOnePhoneNumber;

    private Long driverTwoId;

    private String driverTwo;

    private String driverTwoPhoneNumber;

    private String typeOfUnit;

    private String typeOfDriver;

    private Long unitStatusId;

    private String unitStatus;

    private String unitStatusColor;

    private Long driverStatusId;

    private String driverStatus;

    private String driverStatusColor;

    private String from;

    private String to;

    private String endTime;

    private String loadNumber;

    private Long loadId;

    private String notes;

    private String teamColor;

}
