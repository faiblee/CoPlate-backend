package ru.ssau.tk.faible.coplatebackend.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtCore {

    @Value("${faible.coplatebackend.secret}")
    private String secret;
    @Value("${faible.coplatebackend.lifetime}")
    private int lifetime;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long id, String username) {

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + lifetime))
                .signWith(getSecretKey())
                .compact();
    }


    public String getUsernameFromJwt(String jwt) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
