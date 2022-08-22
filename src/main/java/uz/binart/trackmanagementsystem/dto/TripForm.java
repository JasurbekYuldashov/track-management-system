package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class TripForm{

    private Long id;

    private String customTripNumber;

    @NotNull(message = "driver's id should be specified")
    @Positive(message = "driver's id should be positive")
    private Long driverId;

    private Long secondDriverId;

    @NotNull(message = "truck should be specified")
    @Positive(message = "truck id should be positive")
    private Long truckId;

    @NotNull(message = "loads should be specified")
    @Size(min = 1)
    private List<Long> loadIds;

    private String odometer;

    @Size(max = 1000)
    private String driverInstructions;

}
