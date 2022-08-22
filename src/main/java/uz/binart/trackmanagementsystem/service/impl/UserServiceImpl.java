package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.dto.NewUserDto;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.repository.OwnedCompanyRepository;
import uz.binart.trackmanagementsystem.repository.TeamRepository;
import uz.binart.trackmanagementsystem.repository.UserRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.SequenceService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final TeamRepository teamRepository;
    private final OwnedCompanyRepository ownedCompanyRepository;

    public List<User> findAll(List<Long> ids){
        return userRepository.findAllById(ids);
    }

    public User getCurrentUserFromContext(){
        return  userRepository.findByUsernameAndDeletedFalse(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public Page<User> findFiltered(Long id, String username, Integer roleId, String phone, Pageable pageable){
        return userRepository.findAll(getFilteringSpecification(id, username, roleId, phone), pageable);
    }

    public User createNewUser(NewUserDto newUserDto, Long userId){

        User user = new User();

        if(user.getId() != null){
            throw new IllegalChangeAttemptException("user's is not null");
        }

        if(userRepository.findByUsernameAndDeletedFalse(newUserDto.getUsername()) != null){
            throw new WrongEntityStructureException("user with such id already exists");
        }
        if(newUserDto.getRoleId() == 1){
            throw new WrongEntityStructureException("saving admins is not allowed");
        }

        user.setUsername(newUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
        user.setEmail(newUserDto.getEmail());
        user.setPhone(newUserDto.getPhone());
        user.setRoleId(newUserDto.getRoleId());
        user.setName(newUserDto.getName());
        user.setVisibleIds(newUserDto.getVisibleIds());

        user.setVisibleTeamIds(newUserDto.getVisibleTeamIds());
        sequenceService.updateSequence("users");
        User savedUser = userRepository.save(user);
        actionService.captureCreate(savedUser, "users", userId);
        return savedUser;
    }

    public void deleteById(Long id, Long userId){

        User actor = userRepository.getOne(userId);
        User deleting = userRepository.getOne(id);
        if(deleting == null)
            throw new NotFoundException();
        if(actor.getRoleId() >= deleting.getRoleId())
            throw new IllegalChangeAttemptException("deleting only with lower than actors permissions");
        actionService.captureDelete(deleting, "users", userId);
        deleting.setDeleted(true);
        userRepository.save(deleting);
    }

    public User updateUser(NewUserDto newUserDto, Long userId){
        User user = userRepository.getOne(newUserDto.getId());

        if(newUserDto.getRoleId() == 1){
            throw new WrongEntityStructureException("saving admins is not allowed");
        }

        user.setUsername(newUserDto.getUsername());

        if(newUserDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(newUserDto.getPassword()));

        user.setEmail(newUserDto.getEmail());
        user.setPhone(newUserDto.getPhone());
        user.setRoleId(newUserDto.getRoleId());
        user.setName(newUserDto.getName());

        user.setVisibleIds(newUserDto.getVisibleIds().isEmpty() ? ownedCompanyRepository.getIds(): newUserDto.getVisibleIds());
        user.setVisibleTeamIds(newUserDto.getVisibleTeamIds().isEmpty() ? teamRepository.findAllIds(): newUserDto.getVisibleTeamIds());
        User updatedUser = userRepository.save(user);
        actionService.captureUpdate(user, updatedUser, "users", userId);

        return updatedUser;
    }


    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public User findByUsername(String username){
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    private Specification<User> getFilteringSpecification(Long id, String username, Integer roleId, String phone){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(id != null)
                predicates.add(criteriaBuilder.equal(root.get("id"), id));

            if(username != null)
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));

            predicates.add(criteriaBuilder.notEqual(root.get("roleId"), 1));

            if(phone != null)
                predicates.add(criteriaBuilder.equal(root.get("phone"), "%" + phone + "%"));

            predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("deleted"), false), criteriaBuilder.isNull(root.get("deleted"))));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

}
