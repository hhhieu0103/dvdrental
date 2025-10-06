package com.hieu.dvdrental.film;

import com.hieu.dvdrental.type.MpaaRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilmRepository extends JpaRepository<Film, Integer> {

    Page<Film> findByCategories_Id(Integer categoryId, Pageable pageable);
    Page<Film> findByActors_Id(Integer actorId, Pageable pageable);

    Page<Film> findByRating(MpaaRating rating, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM film WHERE fulltext @@ TO_TSQUERY(:query);")
    Page<Film> findByFullText(@Param("query") String query, Pageable pageable);

    //For checking language_id foreign key before deleting
    boolean existsFilmByLanguageId(Integer languageId);
}