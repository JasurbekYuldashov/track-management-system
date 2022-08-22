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
@Table(name = "loads")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Load implements Serializable {

    @Transient
    static final String sequenceName = "loads_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "owned_company_id")
    private Long ownedCompanyId;

    @Column(name = "custom_load_number")
    private String customLoadNumber;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "trip_id")
    private Long tripId;

    @Type(type = "jsonb")
    @Column(name = "pickups", columnDefinition = "jsonb")
    private List<Long> pickups;

    @Type(type = "jsonb")
    @Column(name = "deliveries", columnDefinition = "jsonb")
    private List<Long> deliveries;

    @Type(type = "jsonb")
    @Column(name = "sorted_pickup_and_delivery_ids", columnDefinition = "jsonb")
    private List<Long> sortedPickupAndDeliveryIds;

    @Column(name = "primary_fee")
    private Float primaryFee;

    @Column(name = "active_pickup_id")
    private Long activePickupId;

    @Column(name = "active_delivery_id")
    private Long activeDeliveryId;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "central_start_time")
    private Long centralStartTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "central_end_time")
    private Long centralEndTime;

    @Column(name = "final_delivery_id")
    private Long finalDeliveryId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "truck_id")
    private Long truckId;

    @Column(name = "bol_id")
    private Long bolId;

    @Column(name = "rate_confirmation_id")
    private Long rateConfirmationId;

    @Column(name = "revised_rate_confirmation_id")
    private Long revisedRateConfirmationId;

    @Column(name = "rc_price")
    private Float rcPrice;

    @Column(name = "revisedRcPrice")
    private Float revisedRcPrice;

    @Column(name = "updated_as_upcoming")
    private Boolean updatedAsUpcoming = false;

    @Column(name = "updated_as_covered")
    private Boolean updatedAsCovered = false;

    @Column(name = "updated_as_history")
    private Boolean updatedAsHistory = false;

    @Column(name = "deleted")
    private Boolean deleted = false;
    /*counting fields */
    //A
    @Column(name = "booked")
    private Float booked = 0F;
    //B
    @Column(name = "dispute")
    private Float dispute = 0F;
    //C
    @Column(name = "detention")
    private Float detention = 0F;
    //D
    @Column(name = "additional")
    private Float additional = 0F;
    //E
    @Column(name = "fine")
    private Float fine = 0F;
    //F = A + B + C + D - E
    @Column(name = "revised_invoice")
    private Float revisedInvoice = 0F;
    //G
    @Column(name = "factoring")
    private Float factoring = 0F;
    //H
    @Column(name = "tafs")
    private Float tafs = 0F;
    //I = G * H
    @Column(name = "netPaid")
    private Float netPaid;

    @Column(name = "created_at")
    private Long createdTime;

    @Column(name = "pickup_delivery_can_be_updated_until")
    private Long pickupDeliveryCanBeUpdatedUntil;

    @Column(name = "action_registered_time")
    private Long actionRegisteredTime;

}
