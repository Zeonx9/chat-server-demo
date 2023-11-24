package com.ade.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Сервис, работающий с JWT токенами
 */
@Service
public class JwtService {
    /**
     * ключ используемый для кодировки паролей
     */
    private final static String SECRET_KEY = "AQRP9b52oiVvvJqeNZPqev5KA9PxuCMmajdfpaidfn342HLL";

    /**
     * проверяет токена на валидность
     * @param token строка представляющая jwt токен
     * @param userDetails пердставляет данные о пользователе
     * @return true если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * создает новый токен для пользователя
     * @param extraClaims дополнительные данные для токена, необязательны
     * @param userDetails данные пользователя
     * @return созданный токен
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        final long DAY_AS_MILLIS = 1000 * 60 * 60 * 24;
        return Jwts
                .builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + DAY_AS_MILLIS))
                .signWith(getSingingKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * Извлекает имя пользователя из строки с токеном
     * @param token токен
     * @return имя пользователя
     * @throws io.jsonwebtoken.ExpiredJwtException если токен просрочен
     */
    public String extractUsername(String token) throws ExpiredJwtException {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Создает токен без дополнительных данных
     * @param userDetails данные пользователя
     * @return сгенерированный токен
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    private Key getSingingKey() {
        byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSingingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimFunction) {
        final Claims claims = extractAllClaims(token);
        return claimFunction.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
