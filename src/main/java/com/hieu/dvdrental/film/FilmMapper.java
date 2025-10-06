package com.hieu.dvdrental.film;

import com.hieu.dvdrental.category.Category;
import com.hieu.dvdrental.category.CategorySummaryDto;
import com.hieu.dvdrental.language.Language;
import com.hieu.dvdrental.language.LanguageDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    FilmSummaryDto toSummaryDto(Film film);
    List<FilmSummaryDto> toSummaryDtoSet(List<Film> film);

    LanguageDto toLanguageDto(Language language);
    Language toLanguageEntity(LanguageDto languageDto);

    CategorySummaryDto toCategoryDto(Category category);
    Category toCategoryEntity(CategorySummaryDto categoryDto);
}
