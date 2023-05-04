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
     * Регистрирует нового пользователя с заданными данными
     * @param request содержит имя, пароль и идентификатор компании
     * @return токен для созданного пользователя
     * @throws NameAlreadyTakenException если имя занято
     * @throws CompanyNotFoundException если не передан идентификатор компании
     */
    public AuthResponse register(RegisterData request) {
        var userByName = userRepository.findByUsername(request.getAuthRequest().getLogin());
        if (userByName.isPresent()) {
            throw new NameAlreadyTakenException("Name: " + request.getAuthRequest().getLogin() + " is taken already");
        }

        User newUser = userRepository.save(setUpUser(request.getAuthRequest()));
        newUser.setRealName(request.getRealName());
        newUser.setSurname(request.getSurname());
        newUser.setDateOfBirth(request.getDateOfBirth());
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
        Company company = companyService.getCompanyByIdOrException(request.getCompanyId());

        return User.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .company(company)
                .build();
    }

    @Transactional
    public List<AuthRequest> registerCompany(CompanyRegisterRequest request) {
        Company company = companyService.registerCompany(
                Company.builder().name(request.getCompanyName()).build()
        );
        List<AuthRequest> resultList = new ArrayList<>();
        for (RegisterData info : request.getEmployeeNameList()) {
            info.getAuthRequest().setCompanyId(company.getId());
            info.getAuthRequest().setPassword(randomSecurePassword());

            register(info);

            resultList.add(info.getAuthRequest());
        }
        return resultList;
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
