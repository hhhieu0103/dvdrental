package com.hieu.dvdrental.category;

import com.hieu.dvdrental.film.Film;

import java.util.List;

public class CategoryDto extends CategorySummaryDto {
    List<Film> films;

    public CategoryDto(Category category, List<Film> films) {
        super(category);
        this.films = films;
    }

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }
}
