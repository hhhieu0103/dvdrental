package com.hieu.dvdrental.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public boolean existsFilmByLanguageId(Integer languageId) {
        return filmRepository.existsFilmByLanguageId(languageId);
    }
}
