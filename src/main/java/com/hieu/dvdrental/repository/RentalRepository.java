package com.hieu.dvdrental.repository;

import com.hieu.dvdrental.entity.Rental;
import org.springframework.data.repository.Repository;

public interface RentalRepository extends Repository<Rental, Integer> {
}