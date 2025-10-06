package com.hieu.dvdrental.language;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;

@Entity
@Table(name = "language")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_id_gen")
    @SequenceGenerator(name = "language_id_gen", sequenceName = "language_language_id_seq", allocationSize = 1)
    @Column(name = "language_id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 20)
    @JdbcTypeCode(Types.CHAR)
    private String name;

    @ColumnDefault("now()")
    @Column(name = "last_update", nullable = false, insertable = false, updatable = false)
    private Instant lastUpdate;

    public Language() {}

    public Language(Integer id, String name, Instant lastUpdate) {
        this.id = id;
        this.name = name;
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

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}