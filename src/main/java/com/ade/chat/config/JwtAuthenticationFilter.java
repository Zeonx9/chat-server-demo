package com.ade.chat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Реализует логику фильтрования реквестов
     * проверяет наличие в реквесте токена авторицации
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

        updateSecurityContextIfNecessary(request, jwt, jwtService.extractUsername(jwt));
        filterChain.doFilter(request, response);
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
