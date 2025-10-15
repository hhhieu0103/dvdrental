package com.hieu.dvdrental.city;

import com.hieu.dvdrental.country.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_id_gen")
    @SequenceGenerator(name = "city_id_gen", sequenceName = "city_city_id_seq", allocationSize = 1)
    @Column(name = "city_id", nullable = false)
    private Integer id;

    @Column(name = "city", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ColumnDefault("now()")
    @Column(name = "last_update", nullable = false, insertable = false, updatable = false)
    private Instant lastUpdate;

    public City() {}

    public City(Integer id, String name, Country country, Instant lastUpdate) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.lastUpdate = lastUpdate;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}