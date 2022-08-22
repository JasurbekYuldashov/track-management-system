package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.TeamGetListDto;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.Team;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.repository.TeamRepository;
import uz.binart.trackmanagementsystem.service.DriverService;
import uz.binart.trackmanagementsystem.service.TeamService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private Map<Long, Team> cache;
    private final String defualtColor = "#FFFFFF";

    @PostConstruct
    void initCache(){
        cache = new HashMap<>();
        List<Team> teams = teamRepository.findAll();
        for(Team team: teams){
            cache.put(team.getId(), team);
        }
    }

    public Team getById(Long id){

        Optional<Team> teamOptional = teamRepository.findById(id);

        if(teamOptional.isPresent())
            return teamOptional.get();
        else
            throw new NotFoundException("team with " + id + " id not found");

    }

    public Map<String, Object> getPage(Pageable pageable){

        Page<Team> teamPage = teamRepository.findAll(pageable);

        List<TeamGetListDto> dtoS = new ArrayList<>(teamPage.getSize());

        for(Team team: teamPage.getContent()){
            TeamGetListDto dto = new TeamGetListDto();
            dto.setId(team.getId());
            dto.setName(team.getName() != null ? team.getName() : "");
            dto.setColor(team.getColor());
            dtoS.add(dto);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("number", teamPage.getNumber());
        map.put("numberOfElements", teamPage.getNumberOfElements());
        map.put("totalPages", teamPage.getTotalPages());
        map.put("data", dtoS);

        return map;
    }

    public List<Team> findVisible(List<Long> ids){
        return teamRepository.findAllProperIds(ids);
    }


    public Map<String, Object> getPageByVisibility(List<Long> visibilityIds){

        List<Team> teams = teamRepository.findAll(getFilteringSpecificationForIds(visibilityIds));

        List<TeamGetListDto> dtoS = teams.stream().map(this::parseToDto).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("number", 0);
        map.put("numberOfElements", dtoS.size());
        map.put("totalPages", 1);
        map.put("data", dtoS);

        return map;

    }


    public Team addNewTeam(String name, String colorCode){

        Team team = new Team();


        team.setName(name);
        team.setColor(colorCode);

        return teamRepository.save(team);
    }

    public Optional<Team> findFiltered(Long userId){
        return teamRepository.findOne(getFilteringSpecification(userId));
    }

    public List<Team> findAll(){
        return teamRepository.findAll();
    }

    public String getTeamColor(Long teamId){
        if(teamId != null)
            return cache.get(teamId).getColor();
        else return defualtColor;
    }


    private Specification<Team> getFilteringSpecificationForIds(List<Long> ids){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for(Long id: ids){
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }

    private Specification<Team> getFilteringSpecification(Long userId){
        return ((root, criteriaQuery, criteriaBuilder) -> {

            return null;

            //return null;
        });
    }

    private TeamGetListDto parseToDto(Team team){
        TeamGetListDto dto = new TeamGetListDto();
        dto.setId(team.getId());
        dto.setName(team.getName() != null ? team.getName() : "");
        dto.setColor(team.getColor());
        return dto;
    }


}
