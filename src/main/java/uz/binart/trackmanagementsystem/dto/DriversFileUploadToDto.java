package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DriversFileUploadToDto {
    private Long stopId;
    private MultipartFile file;
}
