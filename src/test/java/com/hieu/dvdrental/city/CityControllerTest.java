package com.hieu.dvdrental.city;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.dvdrental.config.JacksonConfiguration;
import com.hieu.dvdrental.country.CountryController;
import com.hieu.dvdrental.country.CountryDto;
import com.hieu.dvdrental.country.CountryService;
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

import static com.hieu.dvdrental.country.CountryControllerTest.countryDtoList;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CityController.class)
@Import(JacksonConfiguration.class)
public class CityControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CityService cityService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("lastUpdate").descending());
    private final Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("name").ascending());

    private final Instant updated = Instant.now();

    private final List<CityDto> cityDtoList = List.of(
            new CityDto(1, "Iwakuni", updated, countryDtoList.getFirst()),
            new CityDto(2, "Kurashiki", updated, countryDtoList.get(1)),
            new CityDto(3, "Nagareyama", updated, countryDtoList.get(2)),
            new CityDto(4, "Sagamihara", updated, countryDtoList.get(3)),
            new CityDto(5, "Sasebo", updated, countryDtoList.get(4))
    );

    private final Page<CityDto> dtoPage = new PageImpl<>(cityDtoList, pageable, cityDtoList.size());
    private final Page<CityDto> dtoPageDefault = new PageImpl<>(cityDtoList, defaultPageable, cityDtoList.size());

    static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("name", null, "City name must not be blank"),
                Arguments.of("name", "", "City name must not be blank"),
                Arguments.of("name", "   ", "City name must not be blank"),
                Arguments.of("name", "a".repeat(51), "City name must have less than 50 characters")
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
                Arguments.of("", "City name must not be blank"),
                Arguments.of("   ", "City name must not be blank"),
                Arguments.of("a".repeat(51), "City name must not have more than 50 characters")
        );
    }

    @Test
    public void shouldReturnAPageOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getAllCities(pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/cities")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(cityService).getAllCities(pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getAllCities(defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(cityService).getAllCities(defaultPageable);
    }

    @Test
    public void shouldReturnAPageOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getCitiesByName("an", pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/cities")
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

        verify(cityService).getCitiesByName("an", pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getCitiesByName("an", defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/cities").param("name", "   an    "))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(cityService).getCitiesByName("an", defaultPageable);
    }

    @ParameterizedTest
    @MethodSource("invalidSearchNameProvider")
    public void shouldRejectWhenSearchNameIsNotValidOnGetByName(String fieldValue, String message) throws Exception {
        mockMvc.perform(get("/cities").param("name", fieldValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities"))
                .andExpect(jsonPath("$.properties.requestParam.name").value(message));
    }

    @Test
    public void shouldReturnAPageOnGetByCountry() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getCitiesByCountry(1, pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/cities")
                        .param("countryId", "1")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(cityService).getCitiesByCountry(1, pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetByCountry() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", cityDtoList));
        given(cityService.getCitiesByCountry(1, defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/cities")
                        .param("countryId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(cityService).getCitiesByCountry(1, defaultPageable);
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectInvalidCountryIdOnGetByCountry(Integer id) throws Exception {
        mockMvc.perform(get("/cities").param("countryId", id.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities"))
                .andExpect(jsonPath("$.properties.requestParam.countryId").value("Invalid country ID"));
    }

    @Test
    public void shouldReturnCityOnGetById() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(cityDtoList.getFirst());
        given(cityService.getCityById(1)).willReturn(cityDtoList.getFirst());

        mockMvc.perform(get("/cities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(cityService).getCityById(1);
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCityWithInvalidIdOnGetById(Integer id) throws Exception {
        mockMvc.perform(get("/cities/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.cityId").value("Invalid ID"));
    }

    @Test
    public void shouldRejectNonExistingCityOnGetById() throws Exception {
        given(cityService.getCityById(1)).willThrow(new EntityNotFoundException("City with id " + 1 + " not found"));

        mockMvc.perform(get("/cities/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("City with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));

        verify(cityService).getCityById(1);
    }

    @Test
    public void shouldCreateNewCity() throws Exception {
        given(cityService.addCity(any(CityDto.class))).willReturn(1);

        mockMvc.perform(post("/cities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cityDtoList.getFirst())))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/cities/1"));

        verify(cityService).addCity(any(CityDto.class));
    }

    @Test
    public void shouldRejectNotExistingCountryOnCreate() throws Exception {
        willThrow(new EntityNotFoundException("Country with id " + 1 + " not found"))
                .given(cityService).addCity(any(CityDto.class));

        CityDto cityDto = new CityDto(null, "Hiroshima", null, countryDtoList.getFirst());
        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Country with id 1 not found"))
                .andExpect(jsonPath("$.instance").value("/cities"));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectCityWithInvalidNameOnCreate(String field, String fieldValue, String message) throws Exception {
        CityDto cityDto = new CityDto(null, fieldValue, null, countryDtoList.getFirst());
        mockMvc.perform(post("/cities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cityDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities"))
                .andExpect(jsonPath("$.properties." + field).value(message));
    }

    @Test
    public void shouldUpdateCity() throws Exception {
        CityDto dto =  new CityDto(null, "New name", null, countryDtoList.getFirst());

        mockMvc.perform(patch("/cities/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(cityService).updateCity(any(CityDto.class));
    }

    @Test
    public void shouldRejectNonExistingIdOnUpdate() throws Exception {
        CityDto dto =  new CityDto(null, "New name", null, countryDtoList.getFirst());
        willThrow(new EntityNotFoundException("City with id " + 1 + " not found"))
                .given(cityService).updateCity(any(CityDto.class));

        mockMvc.perform(patch("/cities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("City with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));

        verify(cityService).updateCity(any(CityDto.class));
    }

    @Test
    public void shouldRejectNotExistingCountryOnUpdate() throws Exception {
        willThrow(new EntityNotFoundException("Country with id " + 1 + " not found"))
                .given(cityService).updateCity(any(CityDto.class));

        CityDto cityDto = new CityDto(null, "Hiroshima", null, countryDtoList.getFirst());
        mockMvc.perform(patch("/cities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Country with id 1 not found"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCityWithInvalidIdOnUpdate(Integer id) throws Exception {
        CityDto dto = new CityDto(null, "Vietnam", null, countryDtoList.getFirst());

        mockMvc.perform(patch("/cities/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.cityId").value("Invalid ID"));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectCityWithInvalidNameOnUpdate(String field, String fieldValue, String message) throws Exception {
        CityDto dto =  new CityDto(null, fieldValue, null, countryDtoList.getFirst());

        mockMvc.perform(patch("/cities/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities/1"))
                .andExpect(jsonPath("$.properties.body." + field).value(message));
    }

    @Test
    public void shouldRejectDifferentIdsOnUpdate() throws Exception {
        CityDto dto =  new CityDto(2, "New name", null, countryDtoList.getFirst());

        mockMvc.perform(patch("/cities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("City Ids do not match"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));
    }

    @Test
    public void shouldDeleteCity() throws Exception {
        mockMvc.perform(delete("/cities/1"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectCityWithInvalidIdOnDelete(Integer id) throws Exception {
        mockMvc.perform(delete("/cities/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/cities/" + id))
                .andExpect(jsonPath("$.properties.pathVariable.cityId").value("Invalid ID"));
    }

    @Test
    public void shouldRejectNonExistingIdOnDelete() throws Exception {
        willThrow(new EntityNotFoundException("City with id " + 1 + " not found"))
                .given(cityService).deleteCity(1);

        mockMvc.perform(delete("/cities/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("City with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));
    }

    @Test
    public void shouldRejectCityWithExistingForeignKeyOnDelete() throws Exception {
        willThrow(new IllegalArgumentException("One or more addresses are associated with the city with id 1"))
                .given(cityService).deleteCity(1);

        mockMvc.perform(delete("/cities/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more addresses are associated with the city with id 1"))
                .andExpect(jsonPath("$.instance").value("/cities/1"));
    }
}
