package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import uz.binart.trackmanagementsystem.util.EmergencyContact;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "drivers")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Driver implements Serializable {

    @Transient
    static final String sequenceName = "drivers_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String street;

    private String city;

    @Column(name = "state_province_id")
    private Long stateProvinceId;

    @Column(name = "zip_code")
    private String zipCode;

    private String phone;

    @Column(name = "teammate_phone_number")
    private String teammatePhoneNumber;

    @Column(name = "driver_type_id")
    private Long driverTypeId;

    @Column(name = "teammate_id")
    private Long teammateId;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    private String fax;

    private String email;

    @Column(name = "employer_id")
    private Long employerId;

    @Column(name = "default_payment_type_id")
    private Long defaultPaymentTypeId;

    @Column(name = "social_security_number")
    private String socialSecurityNumber;

    @Column(name = "social_security_file_id")
    private Long socialSecurityFileId;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "license_file_id")
    private Long licenseFileId;

    @Column(name = "license_description")
    private String licenseDescription;

    @Column(name = "license_expiration")
    private Date licenseExpiration;

    @Column(name = "license_expiration_time")
    private Long licenseExpirationTime;

    @Column(name = "license_issued_jurisdiction_id")
    private Long licenseIssuedJurisdictionId;

    @Column(name = "custom_file_id")
    private Long customFileId;

    @Column(name = "note_to_custom_file")
    private Long customFileNote;

    @Column(name = "medical_card_number")
    private String medicalCardNumber;

    @Column(name = "medical_card_file_id")
    private Long medicalCardFileId;

    @Column(name = "medical_card_renewal")
    private Date medicalCardRenewal;

    @Column(name = "medical_card_expiration_time")
    private Long medicalCardExpirationTime;

    @Column(name = "note")
    private String note;

    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "termination_date")
    private Date terminationDate;

    @Column(name = "termination_time")
    private Long terminationTime;

    @Column(name = "truck_type")
    private String truckType;

    @Column(name = "driver_status_id")
    private Long driverStatusId;

    @Column(name = "truck_id")
    private Long truckId;

    @Type(type = "jsonb")
    @Column(name = "emergency_contact_1", columnDefinition = "jsonb")
    EmergencyContact emergencyContact1;

    @Type(type = "jsonb")
    @Column(name = "emergency_contact_2", columnDefinition = "jsonb")
    EmergencyContact emergencyContact2;

    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "custom_status")
    private String customStatus;

    @Type(type = "jsonb")
    @Column(name = "files_with_description", columnDefinition = "jsonb")
    private Map<Long, String> filesWithDescription;

    @Column(name = "current_employer_id")
    private Long currentEmployerId;

    @Column(name = "current_customer_id")
    private Long currentCustomerId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
