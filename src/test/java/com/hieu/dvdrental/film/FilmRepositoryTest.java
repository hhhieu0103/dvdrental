package com.hieu.dvdrental.film;

import com.hieu.dvdrental.config.TestContainersConfig;
import com.hieu.dvdrental.type.MpaaRating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestContainersConfig.class)
public class FilmRepositoryTest {
    @Autowired
    private FilmRepository filmRepository;

    private final Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

    @Test
    public void findByCategories_Id() {
        Page<Film> films = filmRepository.findByCategories_Id(3, this.pageable);
        List<Film> filmList = films.getContent();

        assertEquals(3, filmList.size());

        assertEquals(110, filmList.getFirst().getId());
        assertEquals("Cabin Flash", filmList.getFirst().getTitle());
        assertEquals("A Stunning Epistle of a Boat And a Man who must Challenge a A Shark in A Baloon Factory", filmList.getFirst().getDescription());
        assertEquals(2006, filmList.getFirst().getReleaseYear());
        assertEquals(1, filmList.getFirst().getLanguage().getId());
        assertEquals(4, filmList.getFirst().getRentalDuration().shortValue());
        assertEquals(BigDecimal.valueOf(0.99), filmList.getFirst().getRentalRate());
        assertEquals(53, filmList.getFirst().getLength().shortValue());
        assertEquals(BigDecimal.valueOf(25.99), filmList.getFirst().getReplacementCost());
        assertEquals(MpaaRating.NC_17, filmList.getFirst().getRating());
        assertEquals(Instant.parse("2013-05-26T14:50:58.951Z"), filmList.getFirst().getLastUpdate());
        assertEquals(List.of("Commentaries", "Deleted Scenes"), filmList.getFirst().getSpecialFeatures());
    }

    @Test
    public void findByRating() {
        Page<Film> films = filmRepository.findByRating(MpaaRating.PG_13, this.pageable);
        List<Film> filmList = films.getContent();

        assertEquals(10, filmList.size());
    }
}
