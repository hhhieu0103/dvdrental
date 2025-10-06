package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainersConfig.class)
public class ActorRepositoryTest {
    @Autowired
    private ActorRepository actorRepository;

    private final Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName").ascending());

    @Test
    public void findByFilms_Id(){
        Page<Actor> actors = actorRepository.findByFilms_Id(1, this.pageable);
        List<Actor> actorList = actors.getContent();

        assertEquals(3, actorList.size());

        assertEquals(10, actorList.getFirst().getId());
        assertEquals("Christian", actorList.getFirst().getFirstName());
        assertEquals("Gable", actorList.getFirst().getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.getFirst().getLastUpdate());

        assertEquals(20, actorList.get(1).getId());
        assertEquals("Lucille", actorList.get(1).getFirstName());
        assertEquals("Tracy", actorList.get(1).getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.get(1).getLastUpdate());

        assertEquals(1, actorList.get(2).getId());
        assertEquals("Penelope", actorList.get(2).getFirstName());
        assertEquals("Guiness", actorList.get(2).getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.get(2).getLastUpdate());
    }

    @Test
    public void findByName() {
        Page<Actor> actors = actorRepository.findByName("be", this.pageable);
        List<Actor> actorList = actors.getContent();

        assertEquals(4, actorList.size());

        assertEquals(6, actorList.getFirst().getId());
        assertEquals("Bette", actorList.getFirst().getFirstName());
        assertEquals("Nicholson", actorList.getFirst().getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.getFirst().getLastUpdate());

        assertEquals(12, actorList.get(1).getId());
        assertEquals("Karl", actorList.get(1).getFirstName());
        assertEquals("Berry", actorList.get(1).getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.get(1).getLastUpdate());

        assertEquals(2, actorList.get(2).getId());
        assertEquals("Nick", actorList.get(2).getFirstName());
        assertEquals("Wahlberg", actorList.get(2).getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.get(2).getLastUpdate());

        assertEquals(14, actorList.get(3).getId());
        assertEquals("Vivien", actorList.get(3).getFirstName());
        assertEquals("Bergen", actorList.get(3).getLastName());
        assertEquals(Instant.parse("2013-05-26T14:47:57.62Z"), actorList.get(3).getLastUpdate());
    }

    @Test
    public void existsActorByIdAndFilms_Id() {
        boolean has118 = actorRepository.existsActorByIdAndFilms_Id(11, 118);
        boolean has119 = actorRepository.existsActorByIdAndFilms_Id(11, 119);

        assertTrue(has118);
        assertFalse(has119);
    }
}
