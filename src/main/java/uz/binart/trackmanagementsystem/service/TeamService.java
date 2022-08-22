package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.TeamPostDto;
import uz.binart.trackmanagementsystem.model.Team;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamService {

    Team getById(Long id);

    Map<String, Object> getPage(Pageable pageable);

    List<Team> findVisible(List<Long> ids);

    Map<String, Object> getPageByVisibility(List<Long> visibilityIds);

    Team addNewTeam(String name, String colorCode);

    Optional<Team> findFiltered(Long userId);

    List<Team> findAll();

    String getTeamColor(Long teamId);

}
