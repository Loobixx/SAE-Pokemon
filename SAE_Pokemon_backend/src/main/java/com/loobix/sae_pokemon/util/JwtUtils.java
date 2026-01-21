package com.loobix.sae_pokemon.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    // Exemple de clé de plus de 64 caractères
    private String jwtSecret = "THDShisdhfishdusdtTdslfhsL236789SDfstdsfjdfshdfiuqhefkqelmgfwshdflkqefHLDIHhioquhUguYGLygigjghkfUvJHCGkhgCHGcvkghvhksq";    private int jwtExpirationMs = 86400000; // 24 heures

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Log d'erreur (token expiré, invalide, etc.)
        }
        return false;
    }
}