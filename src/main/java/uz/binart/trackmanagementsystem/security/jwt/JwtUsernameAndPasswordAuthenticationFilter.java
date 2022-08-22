package uz.binart.trackmanagementsystem.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uz.binart.trackmanagementsystem.dto.UsernameAndPasswordAuthenticationRequest;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    public static Map<String, String> map = new ConcurrentHashMap<>();

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, SecretKey secretKey){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            return authenticationManager.authenticate(authentication);
        }catch(IOException exception){
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException /*throws IOException, ServletException */{
        String secretKey = SecretKeyAndPrefix.SecretKey;
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(Integer.parseInt("14"))))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
        String uuid = UUID.randomUUID().toString();
        map.put(uuid, token);
        Cookie cookie = new Cookie("session_id", uuid);
        String header = request.getHeader("app_version");

        for(GrantedAuthority role: authResult.getAuthorities()){
            if(role.getAuthority().startsWith("ROLE_")) {
                response.addHeader("authority", role.getAuthority());
                if(!role.getAuthority().equals("ROLE_DRIVER") && header != null)
                    response.setStatus(403);
            }
        }

        response.addCookie(cookie);
        response.addHeader("Authorization", SecretKeyAndPrefix.prefix + token);
        Map<String, Object> map = new HashMap<>();

        response.getWriter().write("{" +
                    "\"errorMessage\": "  + "\"\"" + "," +
                    "\"timeStamp\": " +  "\"" + new Date().getTime() + "\",\n" +
                    "\"data\": { \"token\" : \"" + SecretKeyAndPrefix.prefix + token + "\"}" +
                "}");

        response.getWriter().flush();
    }
}
