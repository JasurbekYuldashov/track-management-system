package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Deprecated
@Getter
@Setter
@Entity
@Table(name = "deliveries")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Delivery implements Serializable {
    @Transient
    static final String sequenceName = "pickups_and_deliveries_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "consignee_company_id")
    private Long consigneeCompanyId;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "drivers_delivery_time")
    private Long driversDeliveryTime;

    @Column(name = "driver_instructions")
    private String driverInstructions;

    @Column(name = "active")
    private Boolean active = false;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "load_id")
    private Long loadId;

    @Column(name = "pickup_id")
    private Long pickupId;

    @Column(name = "bol")
    private String bol;

    @Column(name = "custom_required_info")
    private String customRequiredInfo;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantity_type_id")
    private Long quantityTypeId;

    @Column(name = "notes")
    private String notes;

    @Column(name = "commodity")
    private String commodity;

    @Type(type = "jsonb")
    @Column(name = "driver_uploads", columnDefinition = "jsonb")
    private List<Long> driverUploads;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
