package uz.binart.trackmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.binart.trackmanagementsystem.util.EmergencyContact;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto implements Serializable {

    @Positive(message = "id cannot be negative")
    Long id;

    private String firstName;
    @NotNull
    @NotBlank
    @Size(min = 1)
    private String lastName;

    private String searchNameText;

    private String street;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    @Positive
    private Long stateProvinceId;

    private String address;

    private String zipCode;

    private String phone;

    private String alternatePhone;

    private String fax;

    private String email;
    @NotNull
    @Positive
    private Long defaultPaymentTypeId;

    private String paymentType;

    private String licenseNumber;

    private String licenseDescription;

    private Long licenseIssuedJurisdictionId;

    private Date licenseExpiration;

    private Long licenseExpirationTime;

    private Long medicalCardExpirationTime;

    private Date medicalCardRenewal;

    private Date hireDate;

    private Date terminationDate;

    private Long terminationTime;

    private String licenseExpirationFormatted;

    private String medicalCardRenewalFormatted;

    private String hireDateFormatted;

    private String terminationDateFormatted;

    private EmergencyContact emergencyContact1;

    private EmergencyContact emergencyContact2;

    private Long socialSecurityFileId;

    private Long licenseFileId;

    private Long medicalCardFileId;

    private Long customFileId;

    private String customFileNote;

    private Long truckId;

    private Map<Long, String> filesWithDescription;

    private Boolean active;

    private Long driverStatusId;

    private String driverStatus;

    private String driverStatusColor;

    private String note;

    private Long teammateId;

    private String fullName;

    private String teammateFullName;

    private List<TripDto> tripDtoS;

    private Long currentEmployerId;

    private Long currentCustomerId;

    private Boolean isActive;

    private List<Long> visibleIds;

}
