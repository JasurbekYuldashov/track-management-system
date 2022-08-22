package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "trips")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trip implements Serializable {

    @Transient
    static final String sequenceName = "trips_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "custom_trip_number")
    private String customTripNumber;

    @Column(name = "employer_id")
    private Long employerId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "second_driver_id")
    private Long secondDriverId;

    @Column(name = "owned_company_id")
    private Long ownedCompanyId;

    @Column(name = "accessory_driver_pay")
    private Float accessoryDriverPay;

    @Column(name = "driver_advance")
    private Float driverAdvance;

    @Column(name = "team_driver_id")
    private Long teamDriverId;

    @Column(name = "accessory_team_driver_pay")
    private Float accessoryTeamDriverPay;

    @Column(name = "team_driver_advance")
    private Float teamDriverAdvance;

    @Column(name = "truck_id")
    private Long truckId;

    @Column(name = "trailer_id")
    private Long trailerId;

    @Column(name = "odometer")
    private String odometer;

    @Column(name = "trip_status_id")
    private Long tripStatusId;

    @Type(type = "jsonb")
    @Column(name = "load_ids", columnDefinition = "jsonb")
    private List<Long> loadIds;

    @Column(name = "active_load_id")
    private Long activeLoadId;

    @Column(name = "stage_sequence_id")
    private Long stageSequenceId;

    @Column(name = "driver_instructions")
    private String driverInstructions;

    @Type(type = "jsonb")
    @Column(name = "driver_uploads", columnDefinition = "jsonb")
    private List<Long> driverUploads;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
