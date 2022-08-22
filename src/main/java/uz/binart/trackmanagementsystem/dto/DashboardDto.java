package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class DashboardDto {

    private String driverName;

    private String teammateName;

    private Long tripNumber;

    private String unitTypeName;

    private String phoneNumber;

    private String customTripNumber;

    private String driversTruckType;

    private String arrivalDate;

    private String numbers;

    private String driverType;

    private String status;

    private String condition;

    private String destination;

    private String note;

    private String deliveryDate;

    private String pickupDate;

    private String customLoadNumber;

    private Float accessoryDriverPay;

    private Long driverId;

    private Long teammateId;

    private Long eldUnTil;

    private Long readyFrom;

}
