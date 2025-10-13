package com.hieu.dvdrental.language;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Page<Language> findByNameContainingIgnoreCase(String name, Pageable pageable);
}