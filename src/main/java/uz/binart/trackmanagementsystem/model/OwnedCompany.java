package uz.binart.trackmanagementsystem.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Entity
@ToString
@Table(name = "owned_companies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OwnedCompany implements Serializable {

    @Transient
    static final String sequenceName = "owned_companies_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "logo_file_id")
    private Long logoFileId;

    @Column(name = "street")
    private String street;

    @Column(name = "apt_suite_other")
    private String aptSuiteOther;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province_id")
    private Long stateProvinceId;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_extension_number")
    private String phoneExtensionNumber;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    @Column(name = "alternate_phone_extension_number")
    private String alternatePhoneExtensionNumber;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String webSite;

    @Column(name = "contact")
    private String contact;

    @Column(name = "notes")
    private String notes;

    @Column(name = "motor_carrier_number")
    private String motorCarrierNumber;

    @Column(name = "tax_id")
    private String taxId;

    @Type(type = "jsonb")
    @Column(name = "file_ids_with_names", columnDefinition = "jsonb")
    private Map<Long, String> files;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
