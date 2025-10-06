package com.hieu.dvdrental.language;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language language);
    Language toEntity(LanguageDto dto);
}
