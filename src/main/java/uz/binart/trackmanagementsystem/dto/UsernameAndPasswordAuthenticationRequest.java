package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class UsernameAndPasswordAuthenticationRequest {
    String username;
    String password;
}
