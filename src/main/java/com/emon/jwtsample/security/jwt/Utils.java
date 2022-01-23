package com.emon.jwtsample.security.jwt;

import com.emon.jwtsample.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class Utils {
    @Value("${com.emon.security.jwt.secret}")
    private String jwtSecret;
    @Value("${com.emon.security.jwt.time.expiration.milliseconds}")
    private long jwtExpirationInMills;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+jwtExpirationInMills))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }catch (SignatureException se){
            log.error("Invalid JWT signature : {}", se.getMessage());
        }catch (MalformedJwtException me){
            log.error("Invalid JWT token : {}", me.getMessage());
        }catch (ExpiredJwtException ee){
            log.error("JWT token is expired : {}", ee.getMessage());
        }catch (UnsupportedJwtException ue){
            log.error("JWT token is unsupported: {}", ue.getMessage());
        }catch (IllegalArgumentException ie){
            log.error("JWT claims string is empty : {}", ie.getMessage());
        }catch (Exception e){
            log.error("UnIdentified exception : {}", e.getMessage());
        }
        return false;
    }
}
