package uz.binart.trackmanagementsystem.service;

import io.jsonwebtoken.JwtException;

public interface TokenRoutineService {

    Long getUserIdByToken(String token) throws JwtException;

}
