package com.ade.chat.auth;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.AuthRequest;
import com.ade.chat.dtos.AuthResponse;
import com.ade.chat.exception.NameAlreadyTakenException;
import com.ade.chat.mappers.UserMapper;
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
    private final UserMapper userMapper;

    /**
     * Регистрирует нового пользователя с заданными данными
     * @param request содержит имя и пароль
     * @return токен для созданного пользователя
     * @throws NameAlreadyTakenException если имя занято
     */
    public AuthResponse register(AuthRequest request) {
        var userByName = userRepository.findByUsername(request.getLogin());
        if (userByName.isPresent()) {
            throw new NameAlreadyTakenException("Name: " + request.getLogin() + " is taken already");
        }

        User newUser = userRepository.save(setUpUser(request));
        return generateResponseWithToken(newUser);
    }

    /**
     * выполняет вход для указанного пользователя
     * @param request содежит логин и пароль пользователя
     * @return токен, если данные верны
     * @throws org.springframework.security.core.AuthenticationException если данные не верны
     */
    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(getAuthToken(request));
        var user = userRepository.findByUsername(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Somehow user authenticated but not found"));

        return generateResponseWithToken(user);
    }

    private static UsernamePasswordAuthenticationToken getAuthToken(AuthRequest request) {
        return new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        );
    }

    private AuthResponse generateResponseWithToken(User user) {
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .user(userMapper.toDto(user))
                .build();
    }

    private User setUpUser(AuthRequest request) {
        return User.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
    }
}
