package com.hieu.dvdrental.city;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByCountryId(Integer id);
    Page<City> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<City> findByCountryId(Integer id, Pageable pageable);
}