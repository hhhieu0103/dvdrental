package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.film.Film;
import com.hieu.dvdrental.film.FilmMapper;
import com.hieu.dvdrental.film.FilmRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

public class ActorService {
    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final ActorMapper actorMapper;
    private final FilmMapper filmMapper;

    public ActorService(ActorRepository actorRepository, FilmRepository filmRepository, ActorMapper actorMapper, FilmMapper filmMapper) {
        this.actorRepository = actorRepository;
        this.filmRepository = filmRepository;
        this.actorMapper = actorMapper;
        this.filmMapper = filmMapper;
    }

    private Page<ActorDto> toPageDto(Page<Actor> actors, boolean includeFilms) {
        return actors.map(actor -> {
            ActorDto actorDto = actorMapper.toDto(actor);
            if (includeFilms) actorDto.setFilms(filmMapper.toSummaryDtoSet(actor.getFilms()));
            return actorDto;
        });
    }

    public Page<ActorDto> getActors(Pageable pageable, boolean includeFilms) {
        Page<Actor> entityPage = actorRepository.findAll(pageable);
        return toPageDto(entityPage, includeFilms);
    }

    public Page<ActorDto> getActors(Pageable pageable) {
        return getActors(pageable, false);
    }

    public Page<ActorDto> getActorsByName(String name, Pageable pageable, boolean includeFilms) {
        Page<Actor> entityPage = actorRepository.findByName(name, pageable);
        return toPageDto(entityPage, includeFilms);
    }

    public Page<ActorDto> getActorsByFilmId(Integer filmId, Pageable pageable, boolean includeFilms) {
        Page<Actor> entityPage = actorRepository.findByFilms_Id(filmId, pageable);
        return toPageDto(entityPage, includeFilms);
    }

    public Optional<ActorDto> getActorById(Integer actorId, boolean includeFilms) {
        return actorRepository.findById(actorId).map(actor -> {
            ActorDto actorDto = actorMapper.toDto(actor);
            if (includeFilms) actorDto.setFilms(filmMapper.toSummaryDtoSet(actor.getFilms()));
            return actorDto;
        });
    }

    public Integer createActor(ActorSummaryDto actorDto) {
        if (actorRepository.existsById(actorDto.getId())) {
            throw new EntityExistsException("Actor with ID " + actorDto.getId() + " already exists");
        }
        Actor actor = actorMapper.fromSummaryDto(actorDto);
        Actor createdActor = actorRepository.save(actor);
        return createdActor.getId();
    }

    public void updateActor(Actor actor) {
        if (!actorRepository.existsById(actor.getId())) {
            throw new EntityNotFoundException("Actor with ID " + actor.getId() + " not found");
        }
        actorRepository.save(actor);
    }

    public void deleteActor(Integer actorId) {
        if (!actorRepository.existsById(actorId)) {
            throw new EntityNotFoundException("Actor with ID " + actorId + " not found");
        }
        actorRepository.deleteById(actorId);
    }

    private Actor getActorById(Integer actorId) {
        return actorRepository.findById(actorId).orElseThrow(() ->
                new EntityNotFoundException("Actor with ID " + actorId + " not found")
        );
    }

    //Todo: move to film service
    private Film getFilmById(Integer filmId) {
        return filmRepository.findById(filmId).orElseThrow(() ->
                new EntityNotFoundException("Film with ID " + filmId + " not found")
        );
    }

    private void validateAssociation(Integer actorId, Integer filmId) {
        if (actorRepository.existsActorByIdAndFilms_Id(actorId, filmId)) {
            throw new EntityExistsException("Actor with ID " + actorId + " already has Film with ID " + filmId);
        }
    }

    public void addFilmToActor(Integer actorId, Integer filmId) {
        validateAssociation(actorId, filmId);
        Actor actor = getActorById(actorId);
        Film film = getFilmById(filmId);
        film.getActors().add(actor);
    }

    public void removeFilmFromActor(Integer actorId, Integer filmId) {
        validateAssociation(actorId, filmId);
        Actor actor = getActorById(actorId);
        Film film = getFilmById(filmId);
        film.getActors().remove(actor);
    }

    private Set<Film> getNewFilms(Actor actor, Set<Integer> filmIds, boolean ignoreAssociatedFilms) {
        Set<Film> newFilms = new HashSet<>(filmRepository.findAllById(filmIds));
        if (newFilms.size() <  filmIds.size()) {
            Set<Integer> notFoundFilmIds = new HashSet<>(filmIds);
            notFoundFilmIds.removeAll(newFilms.stream().map(Film::getId).collect(Collectors.toSet()));
            throw new EntityNotFoundException("Film with ID " + notFoundFilmIds + " not found");
        }

        if (!ignoreAssociatedFilms) {
            Set<Film> associatedFilms = new HashSet<>(actor.getFilms());
            associatedFilms.retainAll(newFilms);
            if (!associatedFilms.isEmpty()) {
                throw new EntityExistsException("Actor with ID " + actor.getId() + " already has Film with ID " + associatedFilms);
            }
        }

        return newFilms;
    }

    private void addNewFilmsToActor(Actor actor, Set<Film> newFilms) {
        Set<Film> filmsToAdd = new HashSet<>(newFilms);
        filmsToAdd.removeAll(actor.getFilms());
        filmsToAdd.forEach(newFilm -> newFilm.getActors().add(actor));
    }

    private void removeNewFilmsFromActor(Actor actor, Set<Film> newFilms) {
        Set<Film> filmsToRemove = new HashSet<>(actor.getFilms());
        filmsToRemove.removeAll(newFilms);
        filmsToRemove.forEach(newFilm -> newFilm.getActors().remove(actor));
    }

    public void addFilmsToActor(Integer actorId, Set<Integer> filmIds, boolean ignoreAssociatedFilms) {
        Actor actor = getActorById(actorId);
        Set<Film> newFilms = getNewFilms(actor, filmIds, ignoreAssociatedFilms);
        addNewFilmsToActor(actor, newFilms);
    }

    public void addFilmsToActor(Integer actorId, Set<Integer> filmIds) {
        addFilmsToActor(actorId, filmIds, true);
    }

    public void removeFilmsFromActor(Integer actorId, Set<Integer> filmIds, boolean ignoreAssociatedFilms) {
        Actor actor = getActorById(actorId);
        Set<Film> newFilms = getNewFilms(actor, filmIds, ignoreAssociatedFilms);
        removeNewFilmsFromActor(actor, newFilms);
    }

    public void removeFilmsFromActor(Integer actorId, Set<Integer> filmIds) {
        removeFilmsFromActor(actorId, filmIds, true);
    }

    public void updateFilms(Integer actorId, Set<Integer> filmIds, boolean ignoreAssociatedFilms) {
        Actor actor = getActorById(actorId);
        Set<Film> newFilms = getNewFilms(actor, filmIds, ignoreAssociatedFilms);
        addNewFilmsToActor(actor, newFilms);
        removeNewFilmsFromActor(actor, newFilms);
    }

    public void updateFilms(Integer actorId, Set<Integer> filmIds) {
        this.updateFilms(actorId, filmIds, true);
    }
}
