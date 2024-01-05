package com.ade.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Дополнительная конфигурация, подключает фильтр и указывает какие запросы должны быть защищены
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Настраивает SecurityFilterChain устанавливая набор енд-поинтов с открытым доступом
     * и закрытые енд-поинты
     * а так же указывает на используемый фильтр
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/chat_api/v1/auth/register").hasAuthority("ADMIN")
                .requestMatchers("/chat_api/v1/auth/company/**").hasAuthority("SUPER_ADMIN")
                .requestMatchers("/chat_api/v1/auth/user/**").authenticated()
                .requestMatchers("/chat_api/v1/users/**").authenticated()
                .requestMatchers("/chat_api/v1/chats/**").authenticated()
                .requestMatchers("/chat_api/v1/group_chat").authenticated()
                .requestMatchers("/chat_api/v1/private_chat/**").authenticated()
                .requestMatchers("/chat_api/v1/company/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
