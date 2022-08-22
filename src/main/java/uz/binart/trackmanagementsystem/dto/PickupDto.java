package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class PickupDto {

    private Long id;
    @NotNull
    private Long shipperCompanyId;

    private String shipperCompany;

    private Date pickupDate;
    @NotNull
    private Long pickupDate_;

    private String pickupDateFormatted;

    private String driverInstructions;

    private Long driversPickupTime;

    private String bol;

    private String customRequiredInfo;

    private Integer weight;

    private Integer quantity;

    private Long quantityTypeId;

    private String notes;

    private String commodity;

    private Long bolId;

    private String consigneeNameAndLocation;

    private Long epochTime;
}
