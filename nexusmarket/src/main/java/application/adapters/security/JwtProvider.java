package application.adapters.security;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Adaptador de salida para emitir y validar tokens JWT. */
@Component
public class JwtProvider implements JwtServicePort {

    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generate(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Para emitir un token es necesario disponer de un usuario registrado (con un ID)..");
        }
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("email", user.getEmail().value())
                .claim("Nombre completo", user.getFullName())
                .claim("documento", user.getDocument().value())
                .claim("role", user.getRole().name())
                .claim("status", user.getStatus().name())
                .signWith(key)
                .compact();
    }

    @Override
    public Long extractUserId(String token) {
        try {
            return Long.valueOf(getClaims(token).getSubject());
        } catch (Exception e) {
            throw new InvalidCredentialsException("Token invalido.");
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}