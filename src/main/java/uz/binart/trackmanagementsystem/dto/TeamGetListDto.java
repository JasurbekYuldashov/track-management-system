package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class TeamGetListDto {
    private Long id;
    private String name;
    private String color;
    private String mainDispatcherName;
}
