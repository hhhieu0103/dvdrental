package com.hieu.dvdrental.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_gen")
    @SequenceGenerator(name = "customer_id_gen", sequenceName = "customer_customer_id_seq", allocationSize = 1)
    @Column(name = "customer_id", nullable = false)
    private Integer id;

    @Column(name = "store_id", nullable = false)
    private Short storeId;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "email", length = 50)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ColumnDefault("true")
    @Column(name = "activebool", nullable = false)
    private Boolean activebool = false;

    @ColumnDefault("('now')")
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @ColumnDefault("now()")
    @Column(name = "last_update", insertable = false, updatable = false)
    private Instant lastUpdate;

    @Column(name = "active")
    private Integer active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getStoreId() {
        return storeId;
    }

    public void setStoreId(Short storeId) {
        this.storeId = storeId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean getActivebool() {
        return activebool;
    }

    public void setActivebool(Boolean activebool) {
        this.activebool = activebool;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

}