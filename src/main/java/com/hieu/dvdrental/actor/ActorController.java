package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.film.FilmRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ActorController {

    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;

    public ActorController(ActorRepository actorRepository, FilmRepository filmRepository) {
        this.actorRepository = actorRepository;
        this.filmRepository = filmRepository;
    }

    @GetMapping("/actors")
    public ResponseEntity<Page<Actor>> getActors(
            @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(actorRepository.findAll(pageable));
    }

    @GetMapping(value = "/actors", params = "name")
    public ResponseEntity<Page<Actor>> getActorsByName(
            @RequestParam
            @NotBlank(message = "Search value must not be blank")
            @Size(max = 45)
            String name,
            @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Actor> actors = actorRepository.findByName(name, pageable);
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/actors/{actorId}")
    public ResponseEntity<Actor> getActor(@PathVariable("actorId") Integer actorId) {
        Optional<Actor> actor = actorRepository.findById(actorId);
        return actor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/actors", params = "filmId")
    public ResponseEntity<Page<Actor>> getActorsByFilm(
            @RequestParam Integer filmId,
            @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        if (filmRepository.existsById(filmId)) {
            return ResponseEntity.ok(actorRepository.findByFilms_Id(filmId, pageable));
        }
        return ResponseEntity.notFound().build();
    }

//    @PostMapping("/actors")
//    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) {
//        if (actor.getId() != null && actorRepository.existsById(actor.getId())) {
//            throw new ApiException(
//                    "User with id " + actor.getId() + " already exists",
//                    "Replace the User ID with a new ID or remove the ID from the user",
//                    HttpStatus.CONFLICT
//            );
//        }
//        Actor createdActor = actorRepository.save(actor);
//        URI uri = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{actorId}")
//                .buildAndExpand(createdActor.getId())
//                .toUri();
//
//        return ResponseEntity.created(uri).build();
//    }

    @PatchMapping("/actors/{actorId}")
    public ResponseEntity<Void> updateActor(@PathVariable("actorId") Integer actorId, @Valid @RequestBody Actor actor) {
        if (actorRepository.existsById(actorId)) {
            actor.setId(actorId);
            actorRepository.save(actor);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/actors/{actorId}")
    public ResponseEntity<Void> deleteActor(@PathVariable("actorId") Integer actorId) {
        if (actorRepository.existsById(actorId)) {
            actorRepository.deleteById(actorId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
