package uz.binart.trackmanagementsystem.security;

public enum ApplicationUserPermission {

    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    DISPATCHER_READ("dispatcher:read"),
    DISPATCHER_WRITE("dispatcher:write"),
    ACCOUNTANT_READ("accountant:read"),
    ACCOUNTANT_WRITE("accountant:write"),
    DRIVER_READ("driver:read"),
    DRIVER_WRITE("driver:write"),
    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }

}