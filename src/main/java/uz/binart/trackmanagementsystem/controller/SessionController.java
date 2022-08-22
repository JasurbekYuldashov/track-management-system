package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import uz.binart.trackmanagementsystem.service.TokenRoutineService;
import uz.binart.trackmanagementsystem.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionController {

    private final TokenRoutineService tokenRoutineService;
    private final UserService userService;

    @GetMapping("/get_by_session_id")
    public ResponseEntity<?> getTokenIfExists(HttpServletRequest request, HttpServletResponse response){

        Cookie sessionId = null;

        for(Cookie cookie: request.getCookies()){
            if(cookie.getName().equals("session_id")){
                sessionId = cookie;
            }
        }
        Map<String, String> map = new HashMap<>();
        if(sessionId != null && JwtUsernameAndPasswordAuthenticationFilter.map.containsKey(sessionId.getValue())){
            Long userId = tokenRoutineService.getUserIdByToken(JwtUsernameAndPasswordAuthenticationFilter.map.get(sessionId.getValue()));
            Optional<User> userOptional = userService.findById(userId);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                map.put("username", user.getUsername());
                map.put("role", defineRole(user.getRoleId()));
            }
            response.addHeader("Authorization", "Bearer " + JwtUsernameAndPasswordAuthenticationFilter.map.get(sessionId.getValue()));
        }else{
            return ResponseEntity.badRequest().body("no such session id");
        }
        return ResponseEntity.ok(map);
    }

    private String defineRole(Integer roleId){
        if(roleId.equals(1))
            return "admin";
        else if(roleId.equals(2))
            return "dispatcher";
        else if(roleId.equals(3))
            return "accountant";
        else return "";
    }

}
