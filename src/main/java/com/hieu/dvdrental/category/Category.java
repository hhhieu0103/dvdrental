package com.hieu.dvdrental.category;

import com.hieu.dvdrental.film.Film;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_gen")
    @SequenceGenerator(name = "category_id_gen", sequenceName = "category_category_id_seq", allocationSize = 1)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @ColumnDefault("now()")
    @Column(name = "last_update", nullable = false, insertable = false, updatable = false)
    private Instant lastUpdate;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Film> films;

    public Category(Integer id, String name, Instant lastUpdate) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }

    public Category() {}

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

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }
}