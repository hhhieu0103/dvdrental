package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.film.Film;
import com.hieu.dvdrental.film.FilmSummaryDto;
import jakarta.validation.Valid;

import java.util.List;

public class ActorDto extends ActorSummaryDto {
    @Valid
    private List<FilmSummaryDto> films;

    public List<FilmSummaryDto> getFilms() {
        return films;
    }

    public void setFilms(List<FilmSummaryDto> films) {
        this.films = films;
    }
}
