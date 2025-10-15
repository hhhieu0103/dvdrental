package com.hieu.dvdrental.city;

import com.hieu.dvdrental.country.CountryDto;
import com.hieu.dvdrental.validation.OnReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.ConvertGroup;

import java.time.Instant;

public class CityDto {
    private Integer id;
    @NotBlank(message = "City name must not be blank")
    @Size(max = 50, message = "City name must have less than 50 characters")
    private String name;
    private Instant lastUpdate;
    @Valid
    @NotNull(message = "Country is required")
    @ConvertGroup(to = OnReference.class)
    private CountryDto country;

    public CityDto() {}

    public CityDto(Integer id, String name, Instant lastUpdate, CountryDto country) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.country = country;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }
}
