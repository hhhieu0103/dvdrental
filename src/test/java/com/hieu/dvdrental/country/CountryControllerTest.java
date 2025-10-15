package com.hieu.dvdrental.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.dvdrental.config.JacksonConfiguration;
import jakarta.persistence.EntityNotFoundException;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CountryController.class)
@Import(JacksonConfiguration.class)
public class CountryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CountryService countryService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("lastUpdate").descending());
    private final Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("name").ascending());

    private final Instant updated = Instant.now();

    private final List<CountryDto> countryDtoList = List.of(
            new CountryDto(1, "Vietnam", updated),
            new CountryDto(2, "Laos", updated),
            new CountryDto(3, "Cambodia", updated),
            new CountryDto(4, "Malaysia", updated),
            new CountryDto(5, "Singapore", updated)
    );

    private final Page<CountryDto> dtoPage = new PageImpl<>(countryDtoList, pageable, countryDtoList.size());
    private final Page<CountryDto> dtoPageDefault = new PageImpl<>(countryDtoList, defaultPageable, countryDtoList.size());

    static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("name", null, "Country name must not be blank"),
                Arguments.of("name", "", "Country name must not be blank"),
                Arguments.of("name", "   ", "Country name must not be blank"),
                Arguments.of("name", "a".repeat(51), "Country name must have less than 50 characters")
        );
    }

    static Stream<Arguments> invalidIdProvider() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1),
                Arguments.of(Integer.MAX_VALUE)
        );
    }

    static Stream<Arguments> invalidSearchNameProvider() {
        return Stream.of(
                Arguments.of("", "Country name must not be blank"),
                Arguments.of("   ", "Country name must not be blank"),
                Arguments.of("a".repeat(51), "Country name must not have more than 50 characters")
        );
    }

    @Test
    public void shouldReturnAPageOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", countryDtoList));
        given(countryService.getAllCountries(pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/countries")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(countryService).getAllCountries(pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", countryDtoList));
        given(countryService.getAllCountries(defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(countryService).getAllCountries(defaultPageable);
    }

    @Test
    public void shouldReturnAPageOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", countryDtoList));
        given(countryService.getCountriesByName("an", pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/countries")
                        .param("name", "   an   ")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(countryService).getCountriesByName("an", pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", countryDtoList));
        given(countryService.getCountriesByName("an", defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/countries").param("name", "   an    "))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(countryService).getCountriesByName("an", defaultPageable);
    }

    @ParameterizedTest
    @MethodSource("invalidSearchNameProvider")
    public void shouldRejectWhenSearchNameIsNotValidOnGetByName(String fieldValue, String message) throws Exception {
        mockMvc.perform(get("/countries").param("name", fieldValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries"))
                .andExpect(jsonPath("$.properties.requestParam.name").value(message));
    }

    @Test
    public void shouldReturnCountryOnGetById() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(countryDtoList.getFirst());
        given(countryService.getCountryById(1)).willReturn(countryDtoList.getFirst());

        mockMvc.perform(get("/countries/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(countryService).getCountryById(1);
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCountryWithInvalidIdOnGetById(Integer id) throws Exception {
        mockMvc.perform(get("/countries/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.countryId").value("Invalid ID"));
    }

    @Test
    public void shouldRejectNonExistingCountryOnGetById() throws Exception {
        given(countryService.getCountryById(1)).willThrow(new EntityNotFoundException("Country with id " + 1 + " not found"));

        mockMvc.perform(get("/countries/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Country with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/countries/1"));

        verify(countryService).getCountryById(1);
    }

    @Test
    public void shouldCreateNewCountry() throws Exception {
        given(countryService.addCountry(any(CountryDto.class))).willReturn(1);

        mockMvc.perform(post("/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(countryDtoList.getFirst())))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/countries/1"));

        verify(countryService).addCountry(any(CountryDto.class));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectCountryWithInvalidNameOnCreate(String field, String fieldValue, String message) throws Exception {
        CountryDto countryDto = new CountryDto(null, fieldValue, null);
        mockMvc.perform(post("/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(countryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries"))
                .andExpect(jsonPath("$.properties." + field).value(message));
    }

    @Test
    public void shouldUpdateCountry() throws Exception {
        CountryDto dto =  new CountryDto(null, "New name", null);

        mockMvc.perform(patch("/countries/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(countryService).updateCountry(any(CountryDto.class));
    }

    @Test
    public void shouldRejectNonExistingIdOnUpdate() throws Exception {
        CountryDto dto =  new CountryDto(null, "New name", null);
        willThrow(new EntityNotFoundException("Country with id " + 1 + " not found"))
                .given(countryService).updateCountry(any(CountryDto.class));

        mockMvc.perform(patch("/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Country with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/countries/1"));

        verify(countryService).updateCountry(any(CountryDto.class));
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCountryWithInvalidIdOnUpdate(Integer id) throws Exception {
        CountryDto dto = new CountryDto(null, "Vietnam", null);

        mockMvc.perform(patch("/countries/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.countryId").value("Invalid ID"));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectCountryWithInvalidNameOnUpdate(String field, String fieldValue, String message) throws Exception {
        CountryDto dto =  new CountryDto(null, fieldValue, null);

        mockMvc.perform(patch("/countries/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries/1"))
                .andExpect(jsonPath("$.properties.body." + field).value(message));
    }

    @Test
    public void shouldRejectDifferentIdsOnUpdate() throws Exception {
        CountryDto dto =  new CountryDto(2, "New name", null);

        mockMvc.perform(patch("/countries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Country Ids do not match"))
                .andExpect(jsonPath("$.instance").value("/countries/1"));
    }

    @Test
    public void shouldDeleteCountry() throws Exception {
        mockMvc.perform(delete("/countries/1"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCountryWithInvalidIdOnDelete(Integer id) throws Exception {
        mockMvc.perform(delete("/countries/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/countries/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.countryId").value("Invalid ID"));
    }

    @Test
    public void shouldRejectNonExistingIdOnDelete() throws Exception {
        willThrow(new EntityNotFoundException("Country with id " + 1 + " not found"))
                .given(countryService).deleteCountry(1);

        mockMvc.perform(delete("/countries/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Country with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/countries/1"));
    }

    @Test
    public void shouldRejectCountryWithExistingForeignKeyOnDelete() throws Exception {
        willThrow(new IllegalArgumentException("One or more films are associated with the country with id 1"))
                .given(countryService).deleteCountry(1);

        mockMvc.perform(delete("/countries/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more films are associated with the country with id 1"))
                .andExpect(jsonPath("$.instance").value("/countries/1"));
    }
}
