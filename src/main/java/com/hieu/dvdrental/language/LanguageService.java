package com.hieu.dvdrental.language;

import com.hieu.dvdrental.film.FilmRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final FilmRepository filmRepository;

    public LanguageService(
            LanguageRepository languageRepository,
            LanguageMapper languageMapper,
            FilmRepository filmRepository) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
        this.filmRepository = filmRepository;
    }

    public Page<LanguageDto> getAllLanguages(Pageable pageable) {
        Page<Language> languages = languageRepository.findAll(pageable);
        return languages.map(languageMapper::toDto);
    }

    public LanguageDto getLanguageById(Integer id) {
        Language language = languageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Language with id " + id + " not found"));
        return languageMapper.toDto(language);
    }

    public Page<LanguageDto> getLanguagesByName(String name, Pageable pageable) {
        Page<Language> languages = languageRepository.findByNameContainingIgnoreCase(name, pageable);
        return languages.map(languageMapper::toDto);
    }

    public Integer addLanguage(LanguageDto languageDto) {
        if (languageDto.getId() != null && languageRepository.existsById(languageDto.getId())) {
            throw new EntityExistsException("Language with id " + languageDto.getId() + " already exists");
        }
        Language language = languageMapper.toEntity(languageDto);
        Language createdLanguage = languageRepository.save(language);
        return createdLanguage.getId();
    }

    public void updateLanguage(LanguageDto languageDto) {
        if (!languageRepository.existsById(languageDto.getId())) {
            throw new EntityNotFoundException("Language with id " + languageDto.getId() + " not found");
        }
        Language language = languageMapper.toEntity(languageDto);
        languageRepository.save(language);
    }

    public void deleteLanguage(Integer id) {
        if (!languageRepository.existsById(id)) {
            throw new EntityNotFoundException("Language with id " + id + " not found");
        }
        if (filmRepository.existsFilmByLanguageId(id)) {
            throw new IllegalArgumentException("One or more films are associated with the language with id " + id);
        }
        languageRepository.deleteById(id);
    }
}
