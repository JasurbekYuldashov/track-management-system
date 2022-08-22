package uz.binart.trackmanagementsystem.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.security.jwt.SecretKeyAndPrefix;
import uz.binart.trackmanagementsystem.service.TokenRoutineService;
import uz.binart.trackmanagementsystem.service.UserService;

@Service
public class TokenRoutineServiceImpl implements TokenRoutineService {
    private String secretKey;
    private String tokenPrefix;
    private UserService userService;

    @Autowired
    public TokenRoutineServiceImpl(UserService userService){
        secretKey = SecretKeyAndPrefix.SecretKey;
        tokenPrefix = SecretKeyAndPrefix.prefix;
        this.userService = userService;
    }

    public Long getUserIdByToken(String token)throws JwtException {
        token = token.replace(tokenPrefix, "");
        Jws<Claims> claims = getClaims(token);
        Claims body = claims.getBody();

        User user = userService.findByUsername(body.getSubject());

        return user.getId();
    }

    private Jws<Claims> getClaims(String token){
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .parseClaimsJws(token);
    }
}


