package com.ade.chat.services;

import com.ade.chat.domain.Company;
import com.ade.chat.exception.CompanyNotFoundException;
import com.ade.chat.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис реализующий логику работы с объектами компаний
 */
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    /**
     * Регистрирует новую компанию
     * @param company содержит информацию о компании
     * @return сохраненную компанию
     */
    public Company registerCompany(Company company) {
        return companyRepository.save(company);
    }

    /**
     * Получить компанию по ее идентификатору
     * @param id идентификатор компании
     * @return найденную компанию
     * @throws CompanyNotFoundException если нет компании с таким идентификатором
     */
    public Company getCompanyByIdOrException(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("no company with id: " + id));
    }
}
