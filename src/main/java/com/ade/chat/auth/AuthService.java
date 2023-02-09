package com.ade.chat.auth;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.User;
import com.ade.chat.exception.NameAlreadyTakenException;
import com.ade.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя с заданными данными
     * @param request содержит имя и пароль
     * @return токен для созданного пользователя
     * @throws NameAlreadyTakenException если имя занято
     */
    public AuthResponse register(AuthRequest request) {
        var userByName = userRepository.findByUsername(request.getLogin());
        if (userByName.isPresent())
            throw new NameAlreadyTakenException("Name: " + request.getLogin() + " is taken already");

        User newUser = User.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * выполняет вход для указанного пользователя
     * @param request содежит логин и пароль пользователя
     * @return токен, если данные верны
     * @throws org.springframework.security.core.AuthenticationException если данные не верны
     */
    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Somehow user authenticated but not found"));

        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
