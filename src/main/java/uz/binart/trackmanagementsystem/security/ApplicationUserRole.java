package uz.binart.trackmanagementsystem.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static uz.binart.trackmanagementsystem.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {

    ADMIN(Sets.newHashSet(ADMIN_READ, ADMIN_WRITE, DISPATCHER_READ, DISPATCHER_WRITE)),
    DISPATCHER(Sets.newHashSet(DISPATCHER_READ, DISPATCHER_WRITE)),
    ACCOUNTANT(Sets.newHashSet(ACCOUNTANT_READ, ACCOUNTANT_WRITE)),
    DRIVER(Sets.newHashSet(DRIVER_READ, DRIVER_WRITE)),
    USER(Sets.newHashSet(USER_READ, USER_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions){
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions(){
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

}
