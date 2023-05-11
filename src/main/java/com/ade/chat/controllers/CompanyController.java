package com.ade.chat.controllers;

import com.ade.chat.dtos.CompanyDto;
import com.ade.chat.mappers.CompanyMapper;
import com.ade.chat.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat_api/v1")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @PostMapping("/company/register")
    public ResponseEntity<CompanyDto> registerCompany(@RequestBody CompanyDto company) {
        return ResponseEntity.ok(companyMapper.toDto(
                companyService.registerCompany(companyMapper.toEntity(company))
        ));
    }
}
