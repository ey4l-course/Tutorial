package com.reminder.Users.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final String SECRET;
    private final Long ACCESSEXPIRY;
    private final Long REFRESHEXPIRY;

    public JwtUtil (@Value("${jwt.secret}") String SECRET,
                    @Value("${jwt.access.expiration}") Long ACCESSEXPIRY,
                    @Value("${jwt.refresh.expiration}") Long REFRESHEXPIRY){
        this.SECRET = SECRET;
        this.ACCESSEXPIRY = ACCESSEXPIRY;
        this.REFRESHEXPIRY = REFRESHEXPIRY;
    }

    private SecretKey getSigningKey () {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateJwtToken (String userName, String role){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESSEXPIRY))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken (String userName){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESHEXPIRY))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserName (String jwtToken){
        return extractClaims(jwtToken, Claims::getSubject);
    }

    public String extractRole (String jwtToken){
        return extractClaims(jwtToken, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaims (String jwtToken, Function<Claims, T> claimResolver){
        return claimResolver.apply(extractAllClaims(jwtToken));
    }

    private Claims extractAllClaims (String jwtToken){
        return (Claims) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean validateJwtToken (String jwtToken){
        return !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired (String jwtToken){
        return extractClaims(jwtToken, Claims::getExpiration).before(new Date());
    }
}
