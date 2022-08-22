package uz.binart.trackmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OwnedCompanyListDto {
    private Long id;
    private String name;
    private String abbreviation;
    private String state;
    private String city;
}
