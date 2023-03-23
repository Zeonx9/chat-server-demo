package com.ade.chat.mappers;

import com.ade.chat.domain.Company;
import com.ade.chat.dtos.CompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper extends GenericMapper<Company, CompanyDto>{
    protected CompanyMapper(ModelMapper mapper) {
        super(mapper);
    }

    @Override
    protected Class<Company> getEntityClass() {
        return Company.class;
    }

    @Override
    protected Class<CompanyDto> getDtoClass() {
        return CompanyDto.class;
    }
}
