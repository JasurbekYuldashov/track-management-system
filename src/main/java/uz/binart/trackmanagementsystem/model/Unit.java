package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "units")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Unit implements Serializable {

    @Transient
    static final String sequenceName = "units_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String number;

    @Column(name = "current_employer_id")
    private Long currentEmployerId;

    @Column(name = "employer_id")
    private Long employerId;

    @Column(name = "unit_type_id")
    private Long unitTypeId;

    @Column(name = "ownership_type_id")
    private Long ownershipTypeId;

    @Column(name = "vin")
    private String vin;

    private String make;

    private String model;

    private String description;

    private Integer year;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "second_driver_id")
    private Long secondDriverId;

    @Column(name = "year_purchased")
    private Integer yearPurchased;

    @Column(name = "unit_status_id")
    private Long unitStatusId;

    @Column(name = "purchased_price")
    private Integer purchasedPrice;

    @Column(name = "license_plate_number")
    private String licensePlateNumber;

    @Column(name = "license_plate_expiration")
    private Date LicensePlateExpiration;

    @Column(name = "license_plate_expiration_time")
    private Long licensePlateExpirationTime;

    @Column(name = "notified_of_license")
    private Boolean notifiedOfLicensePlateExpiration = false;

    @Column(name = "annual_inspection_expiration_time")
    private Long annualInspectionExpirationTime;

    @Column(name = "notified_of_inspection")
    private Boolean notifiedOfInspection = false;

    @Column(name = "registration_expiration_time")
    private Long registrationExpirationTime;

    @Column(name = "notified_of_registration")
    private Boolean notifiedOfRegistration = false;

    @Column(name = "inspection_sticker_expiration")
    private Date inspectionStickerExpiration;

    @Column(name = "inspection_sticker_expiration_time")
    private Long inspectionStickerExpirationTime;

    @Type(type = "jsonb")
    @Column(name = "initial_location", columnDefinition = "jsonb")
    private Map<String, String> initialLocation;

    @Type(type = "jsonb")
    @Column(name = "files", columnDefinition = "jsonb")
    private Map<Long, String> files;

    @Column(name = "last_trip_id")
    private Long lastTripId;

    @Column(name = "last_completed_trip_id")
    private Long lastCompletedTripId;

    @Column(name = "is_active")
    private Boolean  isActive = true;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "pm_by_millage")
    private Integer pmByMillage;

    @Column(name = "pm_by_date")
    private Long pmByDate;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "eld_until")
    private Long eldUnTil;

    @Column(name = "ready_from")
    private Long readyFrom;

}
