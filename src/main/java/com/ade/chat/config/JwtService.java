package com.ade.chat.config;

import io.jsonwebtoken.Claims;
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
        return Jwts
                .builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSingingKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * извлекает имя пользователя из строки с токеном
     * @param token токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * создает токен без дополнительных данных
     * @param userDetails данные пользователя
     * @return сгенерированный токен
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * создать HMAC Ключ из секретной строки
     * @return созданный ключ
     */
    private Key getSingingKey() {
        byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * достает все данные из токена
     * @param token токен
     * @return объект Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSingingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * вспомагательная функция для получение данных из объекта Claims
     * @param token токен
     * @param claimFunction функция для получение нужных данных
     * @return требуемые данные
     */
    private  <T> T extractClaim(String token, Function<Claims, T> claimFunction) {
        final Claims claims = extractAllClaims(token);
        return claimFunction.apply(claims);
    }

    /**
     * достает врема прекращения действия
     * @param token токен
     * @return полученное время
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * проверяет закончился ли срок действия токена
     * @param token токен
     * @return true если срок истек иначе false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
