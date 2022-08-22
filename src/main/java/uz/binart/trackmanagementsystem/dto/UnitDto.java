package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class UnitDto {

    private Long id;
    @NotNull
    private String number;

    private String vin;
    @NotNull
    private Long unitTypeId;

    private Long driverId;

    private Long secondDriverId;

    private String unitName;

    private Long currentEmployerId;

    private Long employerId;

    private String unitTypeName;
    @NotNull
    private Long ownershipTypeId;

    private String ownershipName;

    private String ownershipTypeName;

    private Long unitStatusId;

    private String unitStatusColor;

    private String status;

    private String VIN;

    private String make;

    private String stateProvinceName;

    private String statusName;

    private String model;

    private String description;

    private Integer year;

    private Integer yearPurchased;

    private Integer purchasedPrice;

    private String licensePlateNumber;

    private Date LicensePlateExpiration;

    private String licenseExpirationFormatted;

    private Long licensePlateExpirationTime;

    private String inspectionStickerExpirationFormatted;

    private Date inspectionStickerExpiration;

    private Map<String, String> initialLocation;

    private Map<Long, String> files;

    private Long annualInspectionExpirationTime;

    private Long registrationExpirationTime;

    private Boolean isActive;

    private Long teamId;

    private String notes;

    private Long eldUnTil;

    private Long readyFrom;

    private List<Long> visibleIds;

    private List<Long> visibleTeamIds;

}
