package com.hieu.dvdrental.category;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);

    Category fromDto(CategoryDto categoryDto);
    List<CategoryDto> fromDtoList(List<Category> categories);
}
