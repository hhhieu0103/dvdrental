package com.hieu.dvdrental.country;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable);
}