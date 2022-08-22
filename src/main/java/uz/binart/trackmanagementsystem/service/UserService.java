package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.dto.NewUserDto;
import uz.binart.trackmanagementsystem.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll(List<Long> ids);

    User getCurrentUserFromContext();

    Page<User> findFiltered(Long id, String username, Integer roleId, String phone, Pageable pageable);

    User createNewUser(NewUserDto newUserDto, Long userId);

    void deleteById(Long id, Long userId);

    User updateUser(NewUserDto newUserDto, Long userId);

    Optional<User> findById(Long id);

    User findByUsername(String username);

}
