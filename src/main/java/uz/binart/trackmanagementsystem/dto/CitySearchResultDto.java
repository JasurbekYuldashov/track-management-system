package uz.binart.trackmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CitySearchResultDto {
    private Long id;
    private String nameWithParentAnsi;
}
