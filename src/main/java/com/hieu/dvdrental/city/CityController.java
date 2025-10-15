package com.hieu.dvdrental.city;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@RestController
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/cities")
    public ResponseEntity<Page<CityDto>> getCities(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(cityService.getAllCities(pageable));
    }

    @GetMapping("/cities/{cityId}")
    public ResponseEntity<CityDto> getCityById(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer cityId
    ) {
        return ResponseEntity.ok(cityService.getCityById(cityId));
    }

    @GetMapping(value = "/cities", params = "name")
    public ResponseEntity<Page<CityDto>> getCitiesByName(
            @RequestParam
            @NotBlank(message = "City name must not be blank")
            @Size(max = 50, message = "City name must not have more than 50 characters") String name,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(cityService.getCitiesByName(name.trim(), pageable));
    }

    @GetMapping(value = "/cities", params = "countryId")
    public ResponseEntity<Page<CityDto>> getCitiesByCountry(
            @RequestParam
            @Positive(message = "Invalid country ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid country ID")
            Integer countryId,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(cityService.getCitiesByCountry(countryId, pageable));
    }

    @PostMapping("/cities")
    @Validated(Default.class)
    public ResponseEntity<Void> createCity(@Valid @RequestBody CityDto cityDto) {
        Integer cityId = cityService.addCity(cityDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cityId}")
                .buildAndExpand(cityId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/cities/{cityId}")
    public ResponseEntity<Void> updateCity(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer cityId,
            @Valid @RequestBody CityDto cityDto
    ) {
        if (cityDto.getId() != null && !Objects.equals(cityId, cityDto.getId())) {
            throw new IllegalArgumentException("City Ids do not match");
        }
        cityDto.setId(cityId);
        cityService.updateCity(cityDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cities/{cityId}")
    public ResponseEntity<Void> deleteCity(
            @PathVariable
            @Positive(message = "Invalid ID")
            @Max(value = Integer.MAX_VALUE - 1, message = "Invalid ID") Integer cityId) {
        cityService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }

}
