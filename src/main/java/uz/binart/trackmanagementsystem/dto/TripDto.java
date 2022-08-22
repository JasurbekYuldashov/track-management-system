package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TripDto implements Serializable {

    private Long id;

    private Boolean customId;

    @NotNull
    @Range(min = 0L, message = "Please select positive numbers Only")
    private Long driverId;

    private String driverName;

    private Long ownedCompanyId;

    private String ownedCompanyName;

//    private Float accessoryDriverPay;
//
//    private Float driverAdvance;

    private Long loadId;

    private String rcPrice;

    private Long teammateId;

    private String to;

    private String from;

    private String status;

    private String statusName;

    private String statusColor;

    private Long statusId;

    private Date deliveryDate;

    private Date pickupDate;

    private String pickDateFormatted;

    private String deliveryDateFormatted;

    private Float accessoryTeamDriverPay;

    private Float teamDriverAdvance;

    private Long truckId;

    private String truckNumber;

    private String revisedRcPrice;

    private Date readyAt;

    private Long trailerId;

    private String odometer;

    private List<Long> loadIds;

    private List<LoadDto> loadDtoList;

    private Boolean covered;

    private String teammateName;

    private String loadNumber;

    private String customerName;

    private String unitNumber;

    private String driverStatusName;

    private String unitStatusName;

    private List<Object> chronologicalSequence;

    @Size(max = 1000)
    private String driverInstructions;

    private Long unitId;

    private List<Long> visibleIds;
}
