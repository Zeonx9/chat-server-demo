package com.ade.chat.mappers;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenericMapper<E, D> {
    private final ModelMapper mapper;

    public D toDto(E entity, Class<D> dtoClass) {
        return Objects.isNull(entity) ? null : mapper.map(entity, dtoClass);
    }

    public E toEntity(D dto, Class<E> entityClass) {
        return Objects.isNull(dto) ? null : mapper.map(dto, entityClass);
    }

    public List<D> toDtoList(List<E> entityList, Class<D> dtoClass) {
        return entityList
                .stream()
                .map(entity -> toDto(entity, dtoClass))
                .collect(Collectors.toList());
    }
}
