package uz.binart.trackmanagementsystem.dto;

import lombok.Data;
import uz.binart.trackmanagementsystem.model.OwnedCompany;
import uz.binart.trackmanagementsystem.model.Team;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private String roleName;
    private String phone;
    private String name;
    private String email;
    private List<Long> visibleIds;
    private List<Long> visibleTeamIds;
    private List<OwnedCompany> availableCompanies;
    private List<Team> availableTeams;
}
