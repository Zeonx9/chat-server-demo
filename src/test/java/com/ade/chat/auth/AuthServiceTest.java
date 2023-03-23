package com.ade.chat.auth;

import com.ade.chat.config.JwtService;
import com.ade.chat.domain.User;
import com.ade.chat.dtos.AuthRequest;
import com.ade.chat.exception.NameAlreadyTakenException;
import com.ade.chat.mappers.CompanyMapper;
import com.ade.chat.mappers.UserMapper;
import com.ade.chat.repositories.CompanyRepository;
import com.ade.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private AuthService underTest;
    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private CompanyMapper companyMapper;

    @BeforeEach
    void setUp() {
        underTest = new AuthService(userRepository, companyRepository, jwtService, authManager,
                passwordEncoder, userMapper, companyMapper);
    }

    @Test
    void registerUserWithExistingLoginTrow() {
        //given
        User existing = User.builder().username("Artem").build();
        given(userRepository.findByUsername(existing.getUsername())).willReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> underTest.register(AuthRequest.builder().login(existing.getUsername()).build()))
                .isInstanceOf(NameAlreadyTakenException.class)
                .hasMessageContaining("Name: " + existing.getUsername() + " is taken already");
    }

    @Test
    void canRegisterNewUser() {
        //given
        User newGuy = User.builder().username("Artem").build();
        String token = "token";
        given(userRepository.findByUsername(newGuy.getUsername())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(newGuy);
        given(jwtService.generateToken(newGuy)).willReturn(token);

        // when
        var response = underTest.register(AuthRequest.builder().login(newGuy.getUsername()).build());

        //then
        assertThat(response.getToken()).isEqualTo(token);
    }

    @Test
    void authenticateIsCalled() {
        //given
        AuthRequest request = AuthRequest.builder().login("login").password("password").build();
        given(userRepository.findByUsername(request.getLogin())).willReturn(Optional.of(new User()));
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        //when
        underTest.login(request);

        //then
        verify(authManager).authenticate(captor.capture());
        var authToken = captor.getValue();
        assertThat(authToken.getPrincipal()).isEqualTo(request.getLogin());
        assertThat(authToken.getCredentials()).isEqualTo(request.getPassword());
    }
}