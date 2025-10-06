package com.hieu.dvdrental.actor;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.time.Instant;

public class ActorSummaryDto {
    private Integer id;

    @NotBlank(message = "First name must not be blank")
    @Length(max = 45, message = "First name must be less than or equal to 45 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Length(max = 45, message = "Last name must be less than or equal to 45 characters")
    private String lastName;

    private Instant lastUpdate;

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
}
