package com.hieu.dvdrental.country;

import jakarta.validation.Valid;
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

    @GetMapping("/countries/{id}")
    public ResponseEntity<CountryDto> getCountryById(@PathVariable Integer id) {
        return ResponseEntity.ok(countryService.getCountryById(id));
    }

    @GetMapping(value = "/countries", params = "name")
    public ResponseEntity<Page<CountryDto>> getCountriesByName(
            @RequestParam String name,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(countryService.getCountriesByName(name, pageable));
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
            @PathVariable Integer countryId,
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
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer countryId) {
        countryService.deleteCountry(countryId);
        return ResponseEntity.noContent().build();
    }
}
