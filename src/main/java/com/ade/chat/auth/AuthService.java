package com.ade.chat.auth;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.Company;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.*;
import com.ade.chat.exception.CompanyNotFoundException;
import com.ade.chat.exception.NameAlreadyTakenException;
import com.ade.chat.mappers.CompanyMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.repositories.UserRepository;
import com.ade.chat.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Сервис, обрабатывающий запросы связанные с авторизацией и безопасностью
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    /**
     * Регистрирует нового пользователя с заданными данными, если пароль не задан, то будет создан случайный
     * @param request содержит имя, пароль и идентификатор компании
     * @return данные для входа для нового пользователя
     * @throws NameAlreadyTakenException если имя занято
     * @throws CompanyNotFoundException если не передан идентификатор компании
     */
    public AuthRequest register(RegisterData request) {
        var userByName = userRepository.findByUsername(request.getAuthRequest().getLogin());
        if (userByName.isPresent()) {
            throw new NameAlreadyTakenException("Name: " + request.getAuthRequest().getLogin() + " is taken already");
        }
        if (request.getAuthRequest().getPassword() == null) {
            request.getAuthRequest().setPassword(randomSecurePassword());
        }

        userRepository.save(setUpUser(request));
        return request.getAuthRequest();
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

    /**
     * Пытается выполнить вход, в случае удачи - изменяет пароль
     * @param request содержит данные необходимые для смены пароля.
     * @return новый токен
     */
    public AuthResponse changePassword(ChangePasswordRequest request) {
        AuthResponse response = login(request.getAuthRequest());
        userRepository.updatePasswordById(
                passwordEncoder.encode(request.getNewPassword()),
                response.getUser().getId()
        );
        return response;
    }

    /**
     * метод для регистрации компании вместе со списком пользователей
     * @param request содержит информацию о компании и ее пользователях
     * @return список данных для входа созданных пользователей
     */
    @Transactional
    public List<AuthRequest> registerCompany(CompanyRegisterRequest request) {
        Company company = companyService.registerCompany(
                Company.builder().name(request.getCompanyName()).build()
        );
        List<AuthRequest> resultList = new ArrayList<>();

        resultList.add(registerAdmin(company));

        for (RegisterData info : request.getEmployeeNameList()) {
            info.getAuthRequest().setCompanyId(company.getId());
            resultList.add(register(info));
        }
        return resultList;
    }

    private AuthRequest registerAdmin(Company company) {
        String password = randomSecurePassword();
        User admin = userRepository.save(User.builder()
                .role(Role.ADMIN)
                .username("ADMIN_" + company.getName())
                .password(passwordEncoder.encode(password))
                .company(company)
                .build()
        );
        return new AuthRequest(admin.getUsername(), password, company.getId());
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
                .isAdmin(user.getRole() != Role.USER)
                .build();
    }

    private User setUpUser(RegisterData request) {
        if (request.getAuthRequest().getCompanyId() == null) {
            throw new CompanyNotFoundException("No company Id passed to register request");
        }
        Company company = companyService.getCompanyByIdOrException(request.getAuthRequest().getCompanyId());

        return User.builder()
                .username(request.getAuthRequest().getLogin())
                .password(passwordEncoder.encode(request.getAuthRequest().getPassword()))
                .role(Role.USER)
                .company(company)
                .realName(request.getRealName())
                .surname(request.getSurname())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    private String randomSecurePassword() {
        Random random = new Random();
        final int LEN = 4;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LEN; ++i) {
            sb.append((char) random.nextInt('a', 'z'));
        }
        return sb.toString();
    }
}
