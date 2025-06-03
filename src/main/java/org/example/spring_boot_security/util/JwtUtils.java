package org.example.spring_boot_security.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKey secretKey; // Секретный ключ
    private final long expirationTime; // Время жизни токена

    public JwtUtils() {
        // Генерируем безопасный ключ
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.expirationTime = 86400000; // 24 часа
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Доп. данные в токене
        return Jwts.builder()
                .setClaims(claims) // Устанавливаем claims
                .setSubject(userDetails.getUsername()) // Устанавливаем имя пользователя
                .setIssuedAt(new Date()) // Время создания
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Срок действия
                .signWith(secretKey) // Указываем ключ, подписываем секретным ключом (защита от подделки)
                .compact(); // Собираем токен
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // Парсим токен
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("JWT token is expired or invalid"); // Если невалидный
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Извлекаем имя пользователя
    }
}