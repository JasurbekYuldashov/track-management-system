package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DriverInfoDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String truckNumber;
    private String email;
}
