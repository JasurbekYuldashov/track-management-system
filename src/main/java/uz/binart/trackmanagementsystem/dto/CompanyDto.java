package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CompanyDto {

    private Long id;

    private Long parentId;
    @NotNull
    @NotBlank
    private String companyName;
    @NotNull
    @NotBlank
    private String customerTypeId;

    private String customerType;

    private String street;

    private String aptSuiteOther;

    private String city;

    private Long stateProvinceId;
    @NotNull
    private Long locationId;

    private String zipCode;

    private String phoneNumber;

    private String phoneExtensionNumber;

    private String alternatePhone;

    private String alternatePhoneExtensionNumber;

    private String fax;

    private String email;

    private String webSite;

    private String contact;

    private String notes;

    private CitySearchResultDto cityDto;

    private String motorCarrierNumber;

    private String taxId;
}
