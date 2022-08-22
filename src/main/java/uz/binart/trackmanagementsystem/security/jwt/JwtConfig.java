package uz.binart.trackmanagementsystem.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {
    private String secretKey = "secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768secureAmD1768";
    private String tokenPrefix = "Bearer ";
    private String tokenExpirationAfterDays = "14";

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }
}