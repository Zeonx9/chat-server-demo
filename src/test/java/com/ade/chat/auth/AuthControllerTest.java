package com.ade.chat.auth;

import com.ade.chat.controllers.AuthController;
import com.ade.chat.controllers.SecurityConfigurer;
import com.ade.chat.dtos.AuthRequest;
import com.ade.chat.dtos.ChangePasswordRequest;
import com.ade.chat.dtos.CompanyRegisterRequest;
import com.ade.chat.dtos.RegisterData;
import com.ade.chat.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {SecurityConfigurer.class, AuthController.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AuthService authService;

    @Test
    @WithMockUser(value = "spring")
    void canRegisterUser() throws Exception {
        RegisterData data = new RegisterData();
        mockMvc.perform(
                post("/chat_api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(data))
        );
        verify(authService).register(data);
    }

    @Test
    @WithMockUser(value = "spring")
    void canLoginUser() throws Exception {
        AuthRequest data = new AuthRequest();
        mockMvc.perform(
                post("/chat_api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(data))
        );
        verify(authService).login(data);
    }

    @Test
    @WithMockUser(value = "spring")
    void canChangePasswordOfUser() throws Exception {
        ChangePasswordRequest data = new ChangePasswordRequest();
        mockMvc.perform(put("/chat_api/v1/auth/user/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(data))
        );
        verify(authService).changePassword(data);
    }

    @Test
    @WithMockUser(value = "spring")
    void canRegisterCompanyWithUsers() throws Exception {
        CompanyRegisterRequest data = new CompanyRegisterRequest();
        mockMvc.perform(
                post("/chat_api/v1/auth/company/register/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(data))
        );
        verify(authService).registerCompany(data);
    }

    private String asJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
