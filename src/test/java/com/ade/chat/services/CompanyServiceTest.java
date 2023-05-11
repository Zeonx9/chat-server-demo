package com.ade.chat.services;

import com.ade.chat.domain.Company;
import com.ade.chat.exception.CompanyNotFoundException;
import com.ade.chat.repositories.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    private CompanyService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CompanyService(companyRepository);
    }

    @Test
    public void canGetExistingCompanyById() {
        Company existed = new Company();
        given(companyRepository.findById(existed.getId())).willReturn(Optional.of(existed));
        assertThat(underTest.getCompanyByIdOrException(existed.getId())).isEqualTo(existed);
    }

    @Test
    void ExceptionIsThrown_WhenDidNotExist() {
        Company existed = new Company();
        given(companyRepository.findById(existed.getId())).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getCompanyByIdOrException(existed.getId()))
                .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    void canRegisterTheCompany() {
        Company newCompany = new Company();
        underTest.registerCompany(newCompany);
        verify(companyRepository).save(newCompany);
    }
}