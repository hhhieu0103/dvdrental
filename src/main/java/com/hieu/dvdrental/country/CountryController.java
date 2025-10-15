package com.hieu.dvdrental.country;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class CountryController {
    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/countries")
    public ResponseEntity<Page<CountryDto>> getCountries(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(countryService.getAllCountries(pageable));
    }

    @GetMapping("/countries/{countryId}")
    public ResponseEntity<CountryDto> getCountryById(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer countryId
    ) {
        return ResponseEntity.ok(countryService.getCountryById(countryId));
    }

    @GetMapping(value = "/countries", params = "name")
    public ResponseEntity<Page<CountryDto>> getCountriesByName(
            @RequestParam
            @NotBlank(message = "Country name must not be blank")
            @Size(max = 50, message = "Country name must not have more than 50 characters") String name,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(countryService.getCountriesByName(name.trim(), pageable));
    }

    @PostMapping("/countries")
    public ResponseEntity<Void> createCountry(@Valid @RequestBody CountryDto countryDto) {
        Integer countryId = countryService.addCountry(countryDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{countryId}")
                .buildAndExpand(countryId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/countries/{countryId}")
    public ResponseEntity<Void> updateCountry(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer countryId,
            @Valid @RequestBody CountryDto countryDto
    ) {
        if (countryDto.getId() != null && !Objects.equals(countryId, countryDto.getId())) {
            throw new IllegalArgumentException("Country Ids do not match");
        }
        countryDto.setId(countryId);
        countryService.updateCountry(countryDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/countries/{countryId}")
    public ResponseEntity<Void> deleteCountry(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer countryId) {
        countryService.deleteCountry(countryId);
        return ResponseEntity.noContent().build();
    }

}
