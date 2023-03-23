package com.ade.chat.services;

import com.ade.chat.domain.Company;
import com.ade.chat.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Company registerCompany(Company company) {
        System.out.println();
        return companyRepository.save(company);
    }
}
