package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.film.Film;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "actor")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actor_id_gen")
    @SequenceGenerator(name = "actor_id_gen", sequenceName = "actor_actor_id_seq", allocationSize = 1)
    @Column(name = "actor_id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @ColumnDefault("now()")
    @Column(name = "last_update", nullable = false, insertable = false, updatable = false)
    private Instant lastUpdate;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors")
    private List<Film> films;

    public Actor() {}

    public Actor(Integer id, String firstName, String lastName, Instant lastUpdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastUpdate = lastUpdate;
    }

    public Actor(Integer id, String firstName, String lastName, Instant lastUpdate, List<Film> films) {
        this(id, firstName, lastName, lastUpdate);
        this.films = films;
    }

    public Actor(Actor actor) {
        this(actor.getId(), actor.getFirstName(), actor.getLastName(), actor.getLastUpdate());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Actor actor)) return false;
        return Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : 31;
    }
}