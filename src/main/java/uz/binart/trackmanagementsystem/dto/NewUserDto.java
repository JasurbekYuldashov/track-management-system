package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewUserDto {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String name;
    private Integer roleId;
    private List<Long> visibleIds = new ArrayList<>();
    private List<Long> visibleTeamIds = new ArrayList<>();
}
