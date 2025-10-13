package com.hieu.dvdrental.city;

import com.hieu.dvdrental.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByCountryId(Integer id);
}