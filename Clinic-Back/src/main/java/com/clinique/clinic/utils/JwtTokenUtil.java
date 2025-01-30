package com.clinique.clinic.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;
    public JwtTokenUtil(@Value("${jwt.secret}") String base64Secret) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey); // HS256
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                // Put a single role into "roles" as a List<String>
                .claim("roles", List.of(role))
                .setIssuedAt(new Date())
                // Expiration: 1 day from now
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
