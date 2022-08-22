package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Map;

@Data
public class TestDto {

    @Size(max = 10)
    Map<Long, String> files;

}
