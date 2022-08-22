package uz.binart.trackmanagementsystem.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static uz.binart.trackmanagementsystem.security.ApplicationUserRole.*;

@Repository
public class ApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationUserDaoService(PasswordEncoder encoder, UserRepository userRepository){
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if(user != null){
            ApplicationUser appUser = new ApplicationUser(
                    defineAuthorities(user.getRoleId()),
                    user.getUsername(),
                    user.getPassword(),
                    true,
                    true,
                    true,
                    true);
            return Optional.of(appUser);
        }
        else return Optional.empty();
    }

    private Set<SimpleGrantedAuthority> defineAuthorities(Integer roleId){
        if(roleId.equals(1))
            return ADMIN.getGrantedAuthorities();
        else if(roleId.equals(2))
            return DISPATCHER.getGrantedAuthorities();
        else if (roleId.equals(3))
            return ACCOUNTANT.getGrantedAuthorities();
        else if (roleId.equals(4))
            return DRIVER.getGrantedAuthorities();
        else
            return USER.getGrantedAuthorities();
    }


}