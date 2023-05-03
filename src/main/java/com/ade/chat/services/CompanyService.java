package com.ade.chat.services;

import com.ade.chat.domain.Company;
import com.ade.chat.exception.CompanyNotFoundException;
import com.ade.chat.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Company registerCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company getCompanyByIdOrException(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("no company with id: " + id));
    }
}
