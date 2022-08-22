package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OwnedCompanyDto {

    private Long id;

    private String name;

    private String description;

    private String abbreviation;

    private Long logoFileId;

    private String street;

    private String aptSuiteOther;

    private String city;

    private Long stateProvinceId;

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

    private String motorCarrierNumber;

    private String taxId;

    private Map<Long, String> files;

}
