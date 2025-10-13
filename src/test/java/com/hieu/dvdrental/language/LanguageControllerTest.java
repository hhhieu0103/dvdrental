package com.hieu.dvdrental.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieu.dvdrental.config.JacksonConfiguration;
import jakarta.persistence.EntityExistsException;
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

@WebMvcTest(controllers = LanguageController.class)
@Import(JacksonConfiguration.class)
public class LanguageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LanguageService languageService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("lastUpdate").descending());
    private final Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("name").ascending());

    private final Instant updated = Instant.now();

    private final List<LanguageDto> languageDtoList = List.of(
            new LanguageDto(1, "English", updated),
            new LanguageDto(2, "Italian", updated),
            new LanguageDto(3, "Japanese", updated),
            new LanguageDto(4, "Mandarin", updated),
            new LanguageDto(5, "French", updated)
    );

    private final Page<LanguageDto> dtoPage = new PageImpl<>(languageDtoList, pageable, languageDtoList.size());
    private final Page<LanguageDto> dtoPageDefault = new PageImpl<>(languageDtoList, defaultPageable, languageDtoList.size());

    static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("name", null, "Language name must not be blank"),
                Arguments.of("name", "", "Language name must not be blank"),
                Arguments.of("name", "   ", "Language name must not be blank"),
                Arguments.of("name", "a".repeat(21), "Language name must has less than 20 characters")
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
                Arguments.of("", "Language name must not be blank"),
                Arguments.of("   ", "Language name must not be blank"),
                Arguments.of("a".repeat(21), "Language name must has less than 20 characters")
        );
    }

    @Test
    public void shouldReturnAPageOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", languageDtoList));
        given(languageService.getAllLanguages(pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/languages")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(languageService).getAllLanguages(pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetAll() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", languageDtoList));
        given(languageService.getAllLanguages(defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(languageService).getAllLanguages(defaultPageable);
    }

    @Test
    public void shouldReturnAPageOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", languageDtoList));
        given(languageService.getLanguagesByName("an", pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/languages")
                        .param("name", "an")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastUpdate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPage.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPage.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPage.getNumber()));

        verify(languageService).getLanguagesByName("an", pageable);
    }

    @Test
    public void shouldTrimSearchNameOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", languageDtoList));
        given(languageService.getLanguagesByName("an", pageable)).willReturn(dtoPage);

        mockMvc.perform(get("/languages")
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

        verify(languageService).getLanguagesByName("an", pageable);
    }

    @Test
    public void shouldReturnAPageWithDefaultPaginationOnGetByName() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(Map.of("content", languageDtoList));
        given(languageService.getLanguagesByName("an", defaultPageable)).willReturn(dtoPageDefault);

        mockMvc.perform(get("/languages").param("name", "an"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.size").value(dtoPageDefault.getSize()))
                .andExpect(jsonPath("$.totalElements").value(dtoPageDefault.getTotalElements()))
                .andExpect(jsonPath("$.number").value(dtoPageDefault.getNumber()));

        verify(languageService).getLanguagesByName("an", defaultPageable);
    }

    @ParameterizedTest
    @MethodSource("invalidSearchNameProvider")
    public void shouldRejectWhenSearchNameIsNotValidOnGetByName(String fieldValue, String message) throws Exception {
        mockMvc.perform(get("/languages")
                        .param("name", fieldValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/languages"))
                .andExpect(jsonPath("$.properties.requestParam.name").value(message));
    }

    @Test
    public void shouldReturnLanguageOnGetById() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(languageDtoList.getFirst());
        given(languageService.getLanguageById(1)).willReturn(languageDtoList.getFirst());

        mockMvc.perform(get("/languages/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(languageService).getLanguageById(1);
    }

    @Test
    public void shouldRejectNonExistingLanguageOnGetById() throws Exception {
        given(languageService.getLanguageById(1)).willThrow(new EntityNotFoundException("Language with id " + 1 + " not found"));

        mockMvc.perform(get("/languages/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Language with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/languages/1"));

        verify(languageService).getLanguageById(1);
    }

    @Test
    public void shouldCreateNewLanguage() throws Exception {
        given(languageService.addLanguage(any(LanguageDto.class))).willReturn(1);

        mockMvc.perform(post("/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(languageDtoList.getFirst())))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/languages/1"));

        verify(languageService).addLanguage(any(LanguageDto.class));
    }

    @Test
    public void shouldRejectLanguageWithExistingIdOnCreate() throws Exception {
        given(languageService.addLanguage(any(LanguageDto.class))).willThrow(new EntityExistsException("Language with id " + 1 + " already exists"));

        mockMvc.perform(post("/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDtoList.getFirst())))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Exists"))
                .andExpect(jsonPath("$.detail").value("Language with id " + 1 + " already exists"))
                .andExpect(jsonPath("$.instance").value("/languages"));

        verify(languageService).addLanguage(any(LanguageDto.class));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectLanguageWithInvalidNameOnCreate(String field, String fieldValue, String message) throws Exception {
        LanguageDto languageDto = new LanguageDto(null, fieldValue, null);
        mockMvc.perform(post("/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(languageDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/languages"))
                .andExpect(jsonPath("$.properties." + field).value(message));
    }

    @Test
    public void shouldUpdateLanguage() throws Exception {
        LanguageDto dto =  new LanguageDto(null, "New name", null);

        mockMvc.perform(patch("/languages/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(languageService).updateLanguage(any(LanguageDto.class));
    }

    @Test
    public void shouldRejectNonExistingIdOnUpdate() throws Exception {
        LanguageDto dto =  new LanguageDto(null, "New name", null);
        willThrow(new EntityNotFoundException("Language with id " + 1 + " not found"))
                .given(languageService).updateLanguage(any(LanguageDto.class));

        mockMvc.perform(patch("/languages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Language with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/languages/1"));

        verify(languageService).updateLanguage(any(LanguageDto.class));
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void shouldRejectLanguageWithInvalidNameOnUpdate(String field, String fieldValue, String message) throws Exception {
        LanguageDto dto =  new LanguageDto(null, fieldValue, null);

        mockMvc.perform(patch("/languages/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/languages/1"))
                .andExpect(jsonPath("$.properties.body." + field).value(message));
    }

    @ParameterizedTest
    @MethodSource("invalidIdProvider")
    public void shouldRejectLanguageWithInvalidIdOnUpdate(Integer value) throws Exception {
        LanguageDto dto =  new LanguageDto(null, "Vietnamese", null);

        mockMvc.perform(patch("/languages/" + value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Check the properties for more details"))
                .andExpect(jsonPath("$.instance").value("/languages/" + value))
                .andExpect(jsonPath("$.properties.pathVariable.languageId").value("Invalid ID"));
    }

    @Test
    public void shouldRejectDifferentIdsOnUpdate() throws Exception {
        LanguageDto dto =  new LanguageDto(2, "New name", null);

        mockMvc.perform(patch("/languages/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Language Ids do not match"))
                .andExpect(jsonPath("$.instance").value("/languages/1"));
    }

    @Test
    public void shouldDeleteLanguage() throws Exception {
        mockMvc.perform(delete("/languages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldRejectNonExistingIdOnDelete() throws Exception {
        willThrow(new EntityNotFoundException("Language with id " + 1 + " not found"))
                .given(languageService).deleteLanguage(1);

        mockMvc.perform(delete("/languages/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Entity Not Found"))
                .andExpect(jsonPath("$.detail").value("Language with id " + 1 + " not found"))
                .andExpect(jsonPath("$.instance").value("/languages/1"));
    }

    @Test
    public void shouldRejectLanguageWithExistingForeignKeyOnDelete() throws Exception {
        willThrow(new IllegalArgumentException("One or more films are associated with the language with id 1"))
                .given(languageService).deleteLanguage(1);

        mockMvc.perform(delete("/languages/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more films are associated with the language with id 1"))
                .andExpect(jsonPath("$.instance").value("/languages/1"));
    }
}
