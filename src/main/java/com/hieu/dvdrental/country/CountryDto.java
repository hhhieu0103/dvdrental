package com.hieu.dvdrental.country;

import com.hieu.dvdrental.validation.OnReference;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class CountryDto {
    @NotNull(message = "Country ID must not be null", groups = {OnReference.class})
    private Integer id;
    @NotBlank(message = "Country name must not be blank")
    @Size(max = 50, message = "Country name must have less than 50 characters")
    private String name;
    private Instant lastUpdate;

    public CountryDto(Integer id, String name, Instant lastUpdate) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }

    public CountryDto() {}

    public CountryDto(Integer id) {
        this.id = id;
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
}
