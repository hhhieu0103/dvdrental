package com.hieu.dvdrental.language;

import com.hieu.dvdrental.film.FilmRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private FilmRepository filmRepository;
    private final LanguageMapper languageMapper = Mappers.getMapper(LanguageMapper.class);

    private LanguageService languageService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    private final Instant updated = Instant.now();
    private final List<Language> languageEntityList = List.of(
            new Language(1, "English", updated),
            new Language(2, "Italian", updated),
            new Language(3, "Japanese", updated),
            new Language(4, "Mandarin", updated),
            new Language(5, "French", updated)
    );

    private final List<LanguageDto> languageDtoList = List.of(
            new LanguageDto(1, "English", updated),
            new LanguageDto(2, "Italian", updated),
            new LanguageDto(3, "Japanese", updated),
            new LanguageDto(4, "Mandarin", updated),
            new LanguageDto(5, "French", updated)
    );

    private final Page<Language> entityPage = new PageImpl<>(languageEntityList, pageable, languageEntityList.size());

    @BeforeEach
    void setUp() {
        this.languageService = new LanguageService(languageRepository, languageMapper, filmRepository);
    }

    @Test
    public void shouldReturnALanguagePage() {
        when(languageRepository.findAll(pageable)).thenReturn(entityPage);

        Page<LanguageDto> languagePage = languageService.getAllLanguages(pageable);

        assertThat(languagePage).isNotNull();
        assertThat(languagePage.getContent()).isNotNull();
        assertThat(languagePage.getContent().getFirst().getClass()).isEqualTo(LanguageDto.class);

        assertThat(languagePage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(languageDtoList);
    }

    @Test
    public void shouldReturnLanguageById() {
        when(languageRepository.findById(1)).thenReturn(Optional.of(languageEntityList.getFirst()));

        LanguageDto languageDto = languageService.getLanguageById(1);

        assertThat(languageDto).isNotNull();
        assertThat(languageDto.getClass()).isEqualTo(LanguageDto.class);
        assertThat(languageDto.getId()).isEqualTo(1);
        assertThat(languageDto.getName()).isEqualTo("English");
        assertThat(languageDto.getLastUpdate()).isEqualTo(updated);
    }

    @Test
    public void shouldRejectOnFindByIdWhenIdIsNotExist() {
        when(languageRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> languageService.getLanguageById(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Language with id " + 1 + " not found");
    }

    @Test
    public void shouldReturnALanguagePageByName() {
        when(languageRepository.findByNameContainingIgnoreCase("name", pageable)).thenReturn(entityPage);

        Page<LanguageDto> languagePage = languageService.getLanguagesByName("name", pageable);

        assertThat(languagePage).isNotNull();
        assertThat(languagePage.getContent()).isNotNull();
        assertThat(languagePage.getContent().getFirst().getClass()).isEqualTo(LanguageDto.class);

        assertThat(languagePage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(languageDtoList);
    }

    @Test
    public void shouldCreateLanguage() {
        when(languageRepository.existsById(1)).thenReturn(false);
        when(languageRepository.save(any(Language.class))).thenReturn(languageEntityList.getFirst());

        Integer createdId = languageService.addLanguage(languageDtoList.getFirst());
        assertThat(createdId).isNotNull();
        assertThat(createdId).isEqualTo(1);
    }

    @Test
    public void shouldRejectOnCreateWhenLanguageIdIsExist() {
        when(languageRepository.existsById(1)).thenReturn(true);

        assertThatThrownBy(() -> languageService.addLanguage(languageDtoList.getFirst()))
                .isInstanceOf(EntityExistsException.class)
                .hasMessage("Language with id " + 1 + " already exists");
    }

    @Test
    public void shouldUpdateLanguage() {
        when(languageRepository.existsById(1)).thenReturn(true);

        languageService.updateLanguage(languageDtoList.getFirst());

        verify(languageRepository).save(argThat(entity -> entity.getId() == 1 && entity.getName().equals("English")));
    }

    @Test
    public void shouldRejectOnUpdateWhenLanguageIdIsNotExist() {
        when(languageRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> languageService.updateLanguage(languageDtoList.getFirst()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Language with id " + 1 + " not found");
    }

    @Test
    public void shouldDeleteLanguage() {
        when(languageRepository.existsById(1)).thenReturn(true);
        when(filmRepository.existsFilmByLanguageId(1)).thenReturn(false);

        languageService.deleteLanguage(1);

        verify(languageRepository).deleteById(1);
    }

    @Test
    public void shouldRejectOnDeleteWhenLanguageIdIsNotExist() {
        when(languageRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> languageService.deleteLanguage(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Language with id " + 1 + " not found");
    }

    @Test
    public void shouldRejectOnDeleteWhenExistsFilmWithLanguageId() {
        when(languageRepository.existsById(1)).thenReturn(true);
        when(filmRepository.existsFilmByLanguageId(1)).thenReturn(true);

        assertThatThrownBy(() -> languageService.deleteLanguage(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more films are associated with the language with id 1");
    }
}
