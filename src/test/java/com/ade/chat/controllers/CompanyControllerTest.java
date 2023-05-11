package com.ade.chat.controllers;

import com.ade.chat.domain.Company;
import com.ade.chat.dtos.CompanyDto;
import com.ade.chat.mappers.CompanyMapper;
import com.ade.chat.services.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@ContextConfiguration(classes = {SecurityConfigurer.class, CompanyController.class})
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;
    @MockBean private CompanyMapper companyMapper;

    @Test
    @WithMockUser(value = "spring")
    void canRegisterCompany() throws Exception {
        CompanyDto companyDto = new CompanyDto();
        Company company = new Company();
        Company out = Company.builder().id(35L).build();
        given(companyMapper.toEntity(companyDto)).willReturn(company);
        given(companyService.registerCompany(company)).willReturn(out);

        mockMvc.perform(post("/chat_api/v1/company/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(companyDto))
                .with(csrf())
        ).andExpect(status().isOk());

        verify(companyMapper).toDto(out);
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