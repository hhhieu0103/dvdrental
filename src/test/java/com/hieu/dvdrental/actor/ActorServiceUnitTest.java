package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.category.Category;
import com.hieu.dvdrental.film.Film;
import com.hieu.dvdrental.film.FilmMapper;
import com.hieu.dvdrental.film.FilmRepository;
import com.hieu.dvdrental.film.FilmSummaryDto;
import com.hieu.dvdrental.language.Language;
import com.hieu.dvdrental.type.MpaaRating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActorServiceUnitTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private ActorService actorService;

    private final ActorMapper actorMapper = Mappers.getMapper(ActorMapper.class);
    private final FilmMapper filmMapper = Mappers.getMapper(FilmMapper.class);

    private final Instant updated = Instant.now();
    private final List<Actor> actors = List.of(
            new Actor(1, "Christian", "Gable", updated),
            new Actor(2, "Lucille", "Tracy", updated),
            new Actor(3, "Penelope", "Guiness", updated),
            new Actor(4, "Christian", "Gable", updated),
            new Actor(5, "Lucille", "Tracy", updated),
            new Actor(6, "Lucille", "Tracy", updated),
            new Actor(7, "Penelope", "Guiness", updated)
    );

    private final List<Film> films = List.of(
            new Film(
                    1,
                    "Academy Dinosaur",
                    "A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies",
                    2006,
                    new Language(1, "English", updated),
                    (short) 6,
                    new BigDecimal("0.99"),
                    (short) 86,
                    new BigDecimal("20.99"),
                    updated,
                    List.of("Deleted Scenes","Behind the Scenes"),
                    MpaaRating.PG,
                    List.of(new Category(1, "Action", updated), new Category(2, "Animation", updated))),
            new Film(
                    2,
                    "Ace Goldfinger",
                    "A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China",
                    2006,
                    new Language(1, "English", updated),
                    (short) 3,
                    new BigDecimal("4.99"),
                    (short) 48,
                    new BigDecimal("12.99"),
                    updated,
                    List.of("Deleted Scenes","Trailers"),
                    MpaaRating.G,
                    List.of(new Category(3, "Children", updated), new Category(4, "Classics", updated)))
    );

    private final List<Actor> actorsWithFilms = List.of(
            new Actor(1, "Christian", "Gable", updated, films),
            new Actor(2, "Lucille", "Tracy", updated, films),
            new Actor(3, "Penelope", "Guiness", updated, films),
            new Actor(4, "Christian", "Gable", updated, films),
            new Actor(5, "Lucille", "Tracy", updated, films),
            new Actor(6, "Lucille", "Tracy", updated, films),
            new Actor(7, "Penelope", "Guiness", updated, films)
    );

    private final Pageable pageable = PageRequest.of(1, 3, Sort.by("lastUpdate").descending());
    private final Page<Actor> actorPage = new PageImpl<>(actors, pageable, actors.size());
    private final Page<Actor> actorWithFilmsPage = new  PageImpl<>(actorsWithFilms, pageable, actorsWithFilms.size());

    private final List<ActorDto> actorDtoList = actors.stream().map(actorMapper::toDto).toList();
    private final List<ActorDto> actorDtoWithFilmsList = actorsWithFilms.stream().map(actorMapper::toDto).toList();
//    private final Page<ActorDto> expectedActorPage = new PageImpl<>(actorDtoList, pageable, actorDtoList.size());
//    private final Page<ActorDto> expectedActorWithFilmsListPage = new PageImpl<>(actorDtoWithFilmsList, pageable, actorDtoWithFilmsList.size());

    @Test
    void getActors() {
        when(actorRepository.findAll(pageable)).thenReturn(actorPage);
        Page<ActorDto> actorDtoPage = actorService.getActors(pageable);
        List<ActorDto> actorDtoList = actorDtoPage.getContent();

        assertThat(actorDtoList).usingRecursiveComparison().isEqualTo(actorDtoList);

//        assertNotNull(actorDtoList);
//        assertEquals(ActorDto.class, actorDtoList.getFirst().getClass());
//        assertEquals(1, actorDtoList.getFirst().getId());
//        assertEquals("Christian",  actorDtoList.getFirst().getFirstName());
//        assertEquals("Gable",  actorDtoList.getFirst().getLastName());
//        assertEquals(updated,  actorDtoList.getFirst().getLastUpdate());
//        assertNull(actorDtoList.getFirst().getFilms());
//
//        assertEquals(ActorDto.class, actorDtoList.get(1).getClass());
//        assertEquals(2, actorDtoList.get(1).getId());
//        assertEquals("Lucille",  actorDtoList.get(1).getFirstName());
//        assertEquals("Tracy",  actorDtoList.get(1).getLastName());
//        assertEquals(updated,  actorDtoList.get(1).getLastUpdate());
//        assertNull(actorDtoList.get(1).getFilms());
//
//        assertEquals(ActorDto.class, actorDtoList.get(2).getClass());
//        assertEquals(3, actorDtoList.get(2).getId());
//        assertEquals("Penelope",  actorDtoList.get(2).getFirstName());
//        assertEquals("Guiness",  actorDtoList.get(2).getLastName());
//        assertEquals(updated,  actorDtoList.get(2).getLastUpdate());
//        assertNull(actorDtoList.get(2).getFilms());
    }

    @Test
    void getActorsWithFilms() {
        when(actorRepository.findAll(pageable)).thenReturn(actorWithFilmsPage);
        Page<ActorDto> actorDtoPage = actorService.getActors(pageable, true);
        List<ActorDto> actorDtoList = actorDtoPage.getContent();

        assertEquals(ActorDto.class, actorDtoList.getFirst().getClass());
        assertEquals(1, actorDtoList.getFirst().getId());
        assertEquals("Christian",  actorDtoList.getFirst().getFirstName());
        assertEquals("Gable",  actorDtoList.getFirst().getLastName());
        assertEquals(updated,  actorDtoList.getFirst().getLastUpdate());

        List<FilmSummaryDto> actorFilms = actorDtoList.getFirst().getFilms();
        assertNotNull(actorFilms);
        assertEquals(2, actorFilms.size());

//        assertEquals(FilmSummaryDto.class, actorFilms.getFirst().getClass());
//        assertEquals(1, actorFilms.getFirst().getId());
//        assertEquals("Academy Dinosaur", actorFilms.getFirst().getTitle());
//        assertEquals(1, actorFilms.getFirst().getDescription());
//        assertEquals(1, actorFilms.getFirst().getReleaseYear());
//        assertEquals(1, actorFilms.getFirst().getLanguage());
//        assertEquals(1, actorFilms.getFirst().getRentalDuration());
//        assertEquals(1, actorFilms.getFirst().getRentalRate());
//        assertEquals(1, actorFilms.getFirst().getLength());
//        assertEquals(1, actorFilms.getFirst().getReplacementCost());
//        assertEquals(1, actorFilms.getFirst().getLastUpdate());
//        assertEquals(1, actorFilms.getFirst().getSpecialFeatures());
//        assertEquals(1, actorFilms.getFirst().getRating());
//        assertEquals(1, actorFilms.getFirst().getCategories());

        assertEquals(ActorDto.class, actorDtoList.get(1).getClass());
        assertEquals(2, actorDtoList.get(1).getId());
        assertEquals("Lucille",  actorDtoList.get(1).getFirstName());
        assertEquals("Tracy",  actorDtoList.get(1).getLastName());
        assertEquals(updated,  actorDtoList.get(1).getLastUpdate());
        assertNotNull(actorDtoList.getFirst().getFilms());

        assertEquals(ActorDto.class, actorDtoList.get(2).getClass());
        assertEquals(3, actorDtoList.get(2).getId());
        assertEquals("Penelope",  actorDtoList.get(2).getFirstName());
        assertEquals("Guiness",  actorDtoList.get(2).getLastName());
        assertEquals(updated,  actorDtoList.get(2).getLastUpdate());
        assertNotNull(actorDtoList.getFirst().getFilms());
    }
}
