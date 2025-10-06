package com.hieu.dvdrental.language;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Page<Language> findByNameContainingIgnoreCase(String name, Pageable pageable);
}