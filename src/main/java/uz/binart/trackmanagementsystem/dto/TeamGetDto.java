package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TeamGetDto {
    private Long id;
    private String name;
    private String colorCode;
    private Map<String, Object> mainDispatcher;
    private List<Map<String, Object>> dispatchers;
}




