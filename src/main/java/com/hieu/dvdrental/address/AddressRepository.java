package com.hieu.dvdrental.address;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    boolean existsByCityId(Integer id);
}