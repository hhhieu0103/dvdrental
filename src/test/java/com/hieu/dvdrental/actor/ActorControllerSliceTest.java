package com.hieu.dvdrental.actor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.dvdrental.config.JacksonConfiguration;
import com.hieu.dvdrental.film.FilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActorController.class)
@Import(JacksonConfiguration.class)
public class ActorControllerSliceTest implements ActorControllerTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ActorRepository actorRepository;
    @MockitoBean
    FilmRepository filmRepository;

    private final Pageable pageable = PageRequest.of(1, 5, Sort.by("lastUpdate").descending());
    private final Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("firstName").ascending());

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

    private final Page<Actor> actorPage = new PageImpl<>(actors, pageable, 2);
    private final Page<Actor> actorPageDefaultPagination = new PageImpl<>(actors, defaultPageable, 1);

    private static Stream<Arguments> invalidStringProvider() {
        return Stream.of(
                Arguments.of("firstName", null, "First name must not be blank"),
                Arguments.of("firstName", "", "First name must not be blank"),
                Arguments.of("firstName", "   ", "First name must not be blank"),
                Arguments.of("firstName", "a".repeat(46), "First name must be less than or equal to 45 characters"),
                Arguments.of("lastName", null, "Last name must not be blank"),
                Arguments.of("lastName", "", "Last name must not be blank"),
                Arguments.of("lastName", "   ", "Last name must not be blank"),
                Arguments.of("lastName", "a".repeat(46), "Last name must be less than or equal to 45 characters")
        );
    }

    @Test
    public void shouldReturnActors() throws Exception {
        given(actorRepository.findAll(pageable)).willReturn(actorPage);

        mockMvc.perform(get("/actors?page=1&size=5&sort=lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));

        verify(actorRepository).findAll(pageable);
    }

    @Test
    public void shouldReturnActorsWithDefaultPagination() throws Exception {
        given(actorRepository.findAll(defaultPageable)).willReturn(actorPageDefaultPagination);

        mockMvc.perform(get("/actors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(actorRepository).findAll(defaultPageable);
    }

    @Test
    public void shouldReturnMatchedNameActors() throws Exception {
        String name = "chris";
        given(actorRepository.findByName(name, pageable)).willReturn(actorPage);

        mockMvc.perform(get("/actors")
                        .param("name", name)
                        .param("size", "5")
                        .param("page", "1")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));

        verify(actorRepository).findByName(name, pageable);
    }

    @Test
    public void shouldReturnMatchedNameActorsWithDefaultPagination() throws Exception {
        String name = "chris";
        given(actorRepository.findByName(name, defaultPageable)).willReturn(actorPageDefaultPagination);

        mockMvc.perform(get("/actors").param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(actorRepository).findByName(name, defaultPageable);
    }

    @Test
    public void shouldReturnActorWithExistedActorId() throws Exception {
        given(actorRepository.findById(1)).willReturn(Optional.of(actors.getFirst()));

        mockMvc.perform(get("/actors/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Christian"))
                .andExpect(jsonPath("$.lastName").value("Gable"))
                .andExpect(jsonPath("$.lastUpdate").value(updated.toString()));

        verify(actorRepository).findById(1);
    }

    @Test
    public void shouldRejectNonExistedActorId() throws Exception {
        given(actorRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        mockMvc.perform(get("/actors/9999"))
                .andExpect(status().isNotFound());

        verify(actorRepository).findById(any(Integer.class));
    }

    @Test
    public void shouldReturnActorsFromFilm() throws Exception {
        given(filmRepository.existsById(1)).willReturn(true);
        given(actorRepository.findByFilms_Id(1, pageable)).willReturn(actorPage);

        mockMvc.perform(get("/actors?filmId=1&page=1&size=5&sort=lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));

        verify(filmRepository).existsById(1);
        verify(actorRepository).findByFilms_Id(1, pageable);
    }

    @Test
    public void shouldReturnActorsFromFilmWithDefaultPagination() throws Exception {
        given(filmRepository.existsById(1)).willReturn(true);
        given(actorRepository.findByFilms_Id(1, defaultPageable)).willReturn(actorPageDefaultPagination);

        mockMvc.perform(get("/actors?filmId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].firstName").value("Christian"))
                .andExpect(jsonPath("$.content[0].lastName").value("Gable"))
                .andExpect(jsonPath("$.content[0].lastUpdate").value(updated.toString()))
                .andExpect(jsonPath("$.content.length()").value(7))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(filmRepository).existsById(1);
        verify(actorRepository).findByFilms_Id(1, defaultPageable);
    }

    @Test
    public void shouldRejectNonExistedFilmId() throws Exception {
        given(filmRepository.existsById(any(Integer.class))).willReturn(false);

        mockMvc.perform(get("/actors?filmId=9999"))
                .andExpect(status().isNotFound());

        verify(filmRepository).existsById(any(Integer.class));
    }

    @Test
    public void shouldCreateNewActor() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(false);
        given(actorRepository.save(any(Actor.class))).willReturn(actors.getFirst());

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actors.getFirst())))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/actors/1"));

        verify(actorRepository).existsById(any(Integer.class));
        verify(actorRepository).save(any(Actor.class));
    }

    @Test
    public void shouldRejectExistedIdOnCreate() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(true);

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actors.getFirst())))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("User with id " + actors.getFirst().getId() + " already exists"))
                .andExpect(jsonPath("$.detail").value("Replace the User ID with a new ID or remove the ID from the user"));

        verify(actorRepository).existsById(any(Integer.class));
    }

    @ParameterizedTest
    @MethodSource("invalidStringProvider")
    public void shouldRejectInvalidStringsOnCreate(String fieldName, String invalidValue, String message) throws Exception {
        Actor actor = new Actor(this.actors.getFirst());

        Field field = Actor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(actor, invalidValue);

        mockMvc.perform(post("/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actor)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the invalidFields for more details"))
                .andExpect(jsonPath("$.properties.invalidFields." + fieldName).value(message));
    }

    @Test
    public void shouldUpdateActor() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(true);
        given(actorRepository.save(any(Actor.class))).willReturn(actors.getFirst());

        mockMvc.perform(patch("/actors/{actorId}", actors.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actors.getFirst())))
                .andExpect(status().isNoContent());

        verify(actorRepository).existsById(any(Integer.class));
        verify(actorRepository).save(any(Actor.class));
    }

    @Test
    public void shouldRejectNonExistedIdOnUpdate() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(false);

        mockMvc.perform(patch("/actors/{actorId}", actors.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actors.getFirst())))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidStringProvider")
    public void shouldRejectInvalidStringsOnUpdate(String fieldName, String invalidValue, String message) throws Exception {
        Actor actor = new Actor(this.actors.getFirst());

        Field field = Actor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(actor, invalidValue);

        mockMvc.perform(patch("/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actor)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the invalidFields for more details"))
                .andExpect(jsonPath("$.properties.invalidFields." + fieldName).value(message));
    }

    @Test
    public void shouldDeleteExistedActor() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(true);

        mockMvc.perform(delete("/actors/{actorId}", actors.getFirst().getId()))
                .andExpect(status().isNoContent());

        verify(actorRepository).existsById(any(Integer.class));
    }

    @Test
    public void shouldRejectedNonExistedIdOnDelete() throws Exception {
        given(actorRepository.existsById(any(Integer.class))).willReturn(false);

        mockMvc.perform(delete("/actors/{actorId}", actors.getFirst().getId()))
                .andExpect(status().isNotFound());

        verify(actorRepository).existsById(any(Integer.class));
    }
}
