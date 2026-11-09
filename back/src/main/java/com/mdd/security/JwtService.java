package com.mdd.security;

import com.mdd.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service de gestion des tokens JWT : génération, validation et lecture des informations.
 * <p>
 * Les tokens sont signés en HMAC avec la clé définie par la propriété {@code jwt.secret}
 * et expirent après {@code jwt.expiration-ms} millisecondes.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Construit le service à partir de la configuration.
     *
     * @param secret       clé secrète de signature ({@code jwt.secret})
     * @param expirationMs durée de validité d'un token en millisecondes ({@code jwt.expiration-ms})
     */
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Génère un token JWT signé pour l'utilisateur.
     * Le sujet du token est l'email de l'utilisateur, et l'identifiant est ajouté en claim {@code id}.
     *
     * @param user l'utilisateur pour lequel générer le token
     * @return le token JWT sous forme de chaîne
     */
    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extrait l'email (sujet) contenu dans un token.
     *
     * @param token le token JWT
     * @return l'email de l'utilisateur
     * @throws JwtException si le token est invalide ou expiré
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Vérifie qu'un token est valide (signature correcte et non expiré).
     *
     * @param token le token JWT à tester
     * @return {@code true} si le token est valide, {@code false} sinon
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}