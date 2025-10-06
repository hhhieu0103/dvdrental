package com.hieu.dvdrental.actor;

import com.hieu.dvdrental.ResponsePageImpl;
import com.hieu.dvdrental.config.TestContainersConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
public class ActorControllerIntegrationTest implements ActorControllerTestSuite {

    @Autowired
    private TestRestTemplate restTemplate;

    @Override
    @Test
    public void shouldReturnActors() throws Exception {
        ResponseEntity<ResponsePageImpl<Actor>> response =
                restTemplate.exchange(
                        "/actors?page=0&size=5",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponsePageImpl<Actor>>() {}
                );
        Assertions.assertNotNull(response);
    }

    @Override
    public void shouldReturnActorsWithDefaultPagination() throws Exception {

    }

    @Override
    public void shouldReturnMatchedNameActors() throws Exception {

    }

    @Override
    public void shouldReturnMatchedNameActorsWithDefaultPagination() throws Exception {

    }

    @Override
    public void shouldReturnActorsFromFilm() throws Exception {

    }

    @Override
    public void shouldReturnActorsFromFilmWithDefaultPagination() throws Exception {

    }

    @Override
    public void shouldRejectNonExistedFilmId() throws Exception {

    }

    @Override
    public void shouldReturnActorWithExistedActorId() throws Exception {

    }

    @Override
    public void shouldRejectNonExistedActorId() throws Exception {

    }

    @Override
    public void shouldCreateNewActor() throws Exception {

    }

    @Override
    public void shouldRejectExistedIdOnCreate() throws Exception {

    }

    @Override
    public void shouldRejectInvalidStringsOnCreate(String fieldName, String invalidValue, String message) throws Exception {

    }

    @Override
    public void shouldUpdateActor() throws Exception {

    }

    @Override
    public void shouldRejectNonExistedIdOnUpdate() throws Exception {

    }

    @Override
    public void shouldRejectInvalidStringsOnUpdate(String fieldName, String invalidValue, String message) throws Exception {

    }

    @Override
    public void shouldDeleteExistedActor() throws Exception {

    }

    @Override
    public void shouldRejectedNonExistedIdOnDelete() throws Exception {

    }
}
