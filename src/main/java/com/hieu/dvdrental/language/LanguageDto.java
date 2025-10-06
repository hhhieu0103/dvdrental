package com.hieu.dvdrental.language;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class LanguageDto {
    private Integer id;
    @NotBlank(message = "Language name must not be blank")
    @Size(max = 20, message = "Language name must has less than 20 characters")
    private String name;
    private Instant lastUpdate;

    public LanguageDto(Language language) {
        this.id = language.getId();
        this.name = language.getName();
        this.lastUpdate = language.getLastUpdate();
    }

    public LanguageDto(Integer id, String name, Instant lastUpdate) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }

    public LanguageDto() { }

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
