package com.hieu.dvdrental.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_id_gen")
    @SequenceGenerator(name = "store_id_gen", sequenceName = "store_store_id_seq", allocationSize = 1)
    @Column(name = "store_id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "manager_staff_id", nullable = false)
    private Staff managerStaff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ColumnDefault("now()")
    @Column(name = "last_update", nullable = false, insertable = false, updatable = false)
    private Instant lastUpdate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Staff getManagerStaff() {
        return managerStaff;
    }

    public void setManagerStaff(Staff managerStaff) {
        this.managerStaff = managerStaff;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}