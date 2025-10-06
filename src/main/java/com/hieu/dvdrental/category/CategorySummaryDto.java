package com.hieu.dvdrental.category;

import java.time.Instant;

public class CategorySummaryDto {
    private Integer id;
    private String name;
    private Instant lastUpdate;

    public CategorySummaryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.lastUpdate = category.getLastUpdate();
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
