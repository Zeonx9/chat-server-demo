package com.ade.chat.auth;

import com.ade.chat.dtos.AuthRequest;
import com.ade.chat.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("chat_api/v1/auth")
public class AuthController {

    private final AuthService authService;


    /**
     * POST запрос, который регистрирует нового пользователя
     * @param request - данные для регистрации
     * @return токен полученный после регистрации
     * @throws com.ade.chat.exception.NameAlreadyTakenException если имя уже занято
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * POST запрос, который регистрирует нового пользователя
     * @param request - данные для входа
     * @return токен полученный после входа
     * @throws org.springframework.security.core.AuthenticationException если данные не верны
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
