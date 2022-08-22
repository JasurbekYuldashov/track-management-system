package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class LocationDto {
    private Long id;
    private String name;
    private String ansi;
    private String parentAnsi;
    private Integer firstTimeZone;
    private Integer secondTimeZone;
    private Integer parentTimeZone;
}
