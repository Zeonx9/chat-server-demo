package com.ade.chat.auth;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.Company;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.AuthRequest;
import com.ade.chat.dtos.AuthResponse;
import com.ade.chat.exception.CompanyNotFoundException;
import com.ade.chat.exception.NameAlreadyTakenException;
import com.ade.chat.mappers.CompanyMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.repositories.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    /**
     * Регистрирует нового пользователя с заданными данными
     * @param request содержит имя, пароль и идентификатор компании
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
     * @param request содержит логин и пароль пользователя (идентификатор компании не нужен)
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
                .company(companyMapper.toDto(user.getCompany()))
                .build();
    }

    private User setUpUser(AuthRequest request) {
        if (request.getCompanyId() == null) {
            throw new CompanyNotFoundException("No company Id passed to register request");
        }
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("no company with id: " + request.getCompanyId()));

        return User.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .company(company)
                .build();
    }
}
