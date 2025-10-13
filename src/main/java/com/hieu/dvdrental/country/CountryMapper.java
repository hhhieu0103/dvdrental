package com.hieu.dvdrental.country;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto toDto(Country country);
    Country toEntity(CountryDto countryDto);
}
