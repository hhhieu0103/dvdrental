package com.hieu.dvdrental.actor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActorRepository extends JpaRepository<Actor, Integer> {

    Page<Actor> findByFilms_Id(Integer filmId, Pageable pageable);

    @Query(value = "SELECT a FROM Actor a " +
            "WHERE LOWER(a.firstName) LIKE CONCAT('%', LOWER(:name), '%') OR " +
            "LOWER(a.lastName) LIKE CONCAT('%', LOWER(:name), '%')")
    Page<Actor> findByName(@Param("name") String name, Pageable pageable);

    boolean existsActorByIdAndFilms_Id(Integer actorId, Integer filmId);
}
