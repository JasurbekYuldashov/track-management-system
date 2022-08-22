package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LoadDto implements Serializable {

    private Long id;

    private Long tripId;
    @NotNull
    @NotBlank
    private String customLoadNumber;

    private String customLoadNumber_;
    @NotNull
    private Long customerId;

    private String customer;

    private String from;

    private String to;

    private Date pickupDate;

    private String pickupDateFormatted;

    private Date deliveryDate;

    private String deliveryDateFormatted;

    private String truckNumber;

    private String driverName;
    @Size(min = 1)
    private List<Long> pickups;
    @Size(min = 1)
    private List<Long> deliveries;

    private List<PickupDto> pickupsInitialized;

    private List<DeliveryDto> deliveriesInitialized;

    private Long driverId;

    private Long truckId;

    private Long ownedCompanyId;

    private String ownedCompanyName;

    private Long rateConfirmationId;

    private Long revisedRateConfirmationId;

    private Float rcPrice;

    private Float revisedRcPrice;

    private Boolean pickupsAndDeliveriesCanBeUpdated = true;

    private CitySearchResultDto city;

    private Boolean canBeChanged;

}
