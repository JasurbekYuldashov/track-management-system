package uz.binart.trackmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationListDto {
    private Long id;
    private String ansi;
    private String name;
    private String parentAnsi;
    private String firstTimeZone;
    private String secondTimeZone;
    private String parentTimeZone;
}
