package com.ade.chat.config;

import com.ade.chat.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр запросов, проверяет аутентификацию пользователей
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Реализует логику фильтрования реквестов
     * проверяет наличие в реквесте токена авторизации
     * для полученного токена строит новый токен аутентификации в случае валидности токена
     * и обновляет контекст в SecurityContextHolder
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String jwt = extractJwt(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userName;
        try {
            userName = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            handleResponse(response, e);
            return;
        }

        updateSecurityContextIfNecessary(request, jwt, userName);
        filterChain.doFilter(request, response);
    }

    private static void handleResponse(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private static String extractJwt(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void updateSecurityContextIfNecessary(HttpServletRequest request, String jwt, String username) {
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtService.isTokenValid(jwt, userDetails)) {
            updateSecurityContextWithUserDetails(request, userDetails);
        }
    }

    private static void updateSecurityContextWithUserDetails(HttpServletRequest request, UserDetails userDetails) {
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
