package com.hieu.dvdrental.language;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@RestController
public class LanguageController {

    LanguageService languageService;

    @Autowired
    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping("/languages")
    public ResponseEntity<Page<LanguageDto>> getLanguages(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(languageService.getAllLanguages(pageable));
    }

    @GetMapping("/languages/{languageId}")
    public ResponseEntity<LanguageDto> getLanguageById(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID")
            Integer languageId
    ) {
        return ResponseEntity.ok(languageService.getLanguageById(languageId));
    }

    @GetMapping(value = "/languages", params = "name")
    public ResponseEntity<Page<LanguageDto>> getLanguagesByName(
            @RequestParam
            @NotBlank(message = "Language name must not be blank")
            @Size(max = 20, message = "Language name must not have more than 20 characters") String name,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(languageService.getLanguagesByName(name.trim(), pageable));
    }

    @PostMapping("/languages")
    public ResponseEntity<Void> createLanguage(@Valid @RequestBody LanguageDto languageDto) {
        Integer languageId = languageService.addLanguage(languageDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{languageId}")
                .buildAndExpand(languageId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/languages/{languageId}")
    public ResponseEntity<Void> updateLanguage(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer languageId,
            @Valid @RequestBody LanguageDto languageDto
    ) {
        if (languageDto.getId() != null && !Objects.equals(languageId, languageDto.getId())) {
            throw new IllegalArgumentException("Language Ids do not match");
        }
        languageDto.setId(languageId);
        languageService.updateLanguage(languageDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/languages/{languageId}")
    public ResponseEntity<Void> deleteLanguage(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer languageId) {
        languageService.deleteLanguage(languageId);
        return ResponseEntity.noContent().build();
    }
}
