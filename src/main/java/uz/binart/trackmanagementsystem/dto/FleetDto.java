package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class FleetDto {

    private Long id;

    private String number;

    private Long ownedCompanyId;

    private String ownedCompanyName;

    private String ownedCompanyAbbreviation;

    private String ownershipType;

    private Long licenseExpiration;

    private Integer pmByMillage;

    private Long pmByDate;

    private String vinNumber;

    private String status;

    private String statusColor;

}
