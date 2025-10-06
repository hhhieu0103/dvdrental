package com.hieu.dvdrental.repository;

import com.hieu.dvdrental.entity.Customer;
import org.springframework.data.repository.Repository;

public interface CustomerRepository extends Repository<Customer, Integer> {
}