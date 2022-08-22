package uz.binart.trackmanagementsystem.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class DeliveryDto {

    private Long id;
    @NotNull
    private Long consigneeCompanyId;

    private String consigneeCompany;

    private Date deliveryDate;
    @NotNull
    private Long deliveryDate_;

    private String deliveryDateFormatted;

    private String driverInstructions;

    private Boolean active = false;

    private Boolean completed = false;

    private Long loadId;

    private Long pickupId;

    private String bol;

    private String customRequiredInfo;

    private Integer weight;

    private Integer quantity;

    private Long quantityTypeId;

    private String notes;

    private String commodity;

    private String consigneeNameAndLocation;

    private Long epochTime;

    private Long driversDeliveryTime;

}
