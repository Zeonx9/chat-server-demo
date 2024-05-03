package com.ade.chat.controllers;

import com.ade.chat.dtos.*;
import com.ade.chat.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Отвечает за запросы связанные с авторизацией и безопасностью
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("chat_api/v1/auth")
public class AuthController {

    private final AuthService authService;


    /**
     * Регистрирует нового пользователя, требует уровень доступа ADMIN
     * @param request данные для регистрации
     * @return токен полученный после регистрации
     * @throws com.ade.chat.exception.NameAlreadyTakenException если имя уже занято
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/register")
    public ResponseEntity<AuthRequest> register(@RequestBody RegisterData request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Авторизует пользователя по логину и паролю
     * @param request данные для входа
     * @return токен полученный после входа
     * @throws org.springframework.security.core.AuthenticationException если данные не верны
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Изменяет пароль на указанный, требует предоставления текущих данных для авторизации, требует уровня доступа USER
     * @param passwordRequest DTO, содержащий старые данные для входа, чтобы подтвердить личность и новый пароль
     * @return новые данные для входа или ошибку.
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/user/password")
    @Transactional
    public ResponseEntity<AuthResponse> changePassword(@RequestBody ChangePasswordRequest passwordRequest) {
        return ResponseEntity.ok(authService.changePassword(passwordRequest));
    }


    /**
     * Создает новую компанию и регистрирует в ней пользователей с заданными личными данными, требует уровень доступа SUPER_ADMIN
     * Пароли генерируются автоматически и представляют собой случайную последовательность букв.
     * Этот пароль может быть изменен позже.
     * @param request содержит информацию о новой компании и пользователях
     * @return список данных для авторизации.
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("company/register/users")
    public ResponseEntity<List<AuthRequest>> registerCompany(@RequestBody CompanyRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCompany(request));
    }
}
