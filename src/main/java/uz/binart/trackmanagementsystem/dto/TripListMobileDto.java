package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TripListMobileDto {

    private Long id;
    private String driverTypeName;
    private Float rcPrice;
    private String odometer;
    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;
    private List<Map<String, Object>> stages;

}
