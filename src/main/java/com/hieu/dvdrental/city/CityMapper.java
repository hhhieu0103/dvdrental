package com.hieu.dvdrental.city;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {
    CityDto toDto(City city);
    City toEntity(CityDto cityDto);
}
