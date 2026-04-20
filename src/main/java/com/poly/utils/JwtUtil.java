package com.poly.utils;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey; 

@Component
public class JwtUtil {

    private final String SECRET = "blackfloydcantbreathebecausea12kneesonhisneckforusingacounterfeitmoneytobuyabanana"; // ≥32 chars
    private final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    
    //Generate token WITH roles
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000)) //2 hours
                .signWith(KEY)
                .compact();
    }

    //Extract username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract roles
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    //Validate token
    public boolean isValid(String token) {
        try {
            extractAllClaims(token); // will throw if invalid/expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Internal helper
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}