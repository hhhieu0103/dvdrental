package com.hieu.dvdrental.language;

import com.hieu.dvdrental.ResponsePageImpl;
import com.hieu.dvdrental.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
public class LanguageIntegrationTest implements LanguageTestSuit {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Override
    public void shouldReturnAPageOnGetAll() {
        ResponseEntity<ResponsePageImpl<LanguageDto>> response = restTemplate.exchange(
                "/languages?page=0&size=5&sort=name,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(5);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(5);
        assertThat(response.getBody().getTotalElements()).isEqualTo(6);
        assertThat(response.getBody().getTotalPages()).isEqualTo(2);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<LanguageDto> languages = response.getBody().getContent();
        assertThat(languages.size()).isEqualTo(5);

        assertThat(languages.getFirst().getId()).isEqualTo(1);
        assertThat(languages.getFirst().getName()).isEqualTo("English             ");
        assertThat(languages.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(1).getId()).isEqualTo(5);
        assertThat(languages.get(1).getName()).isEqualTo("French              ");
        assertThat(languages.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(2).getId()).isEqualTo(6);
        assertThat(languages.get(2).getName()).isEqualTo("German              ");
        assertThat(languages.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(3).getId()).isEqualTo(2);
        assertThat(languages.get(3).getName()).isEqualTo("Italian             ");
        assertThat(languages.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(4).getId()).isEqualTo(3);
        assertThat(languages.get(4).getName()).isEqualTo("Japanese            ");
        assertThat(languages.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageWithDefaultPaginationOnGetAll() {
        ResponseEntity<ResponsePageImpl<LanguageDto>> response = restTemplate.exchange(
                "/languages",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(10);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(6);
        assertThat(response.getBody().getTotalElements()).isEqualTo(6);
        assertThat(response.getBody().getTotalPages()).isEqualTo(1);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isTrue();

        assertThat(response.getBody().getContent()).isNotNull();
        List<LanguageDto> languages = response.getBody().getContent();
        assertThat(languages.size()).isEqualTo(6);

        assertThat(languages.getFirst().getId()).isEqualTo(1);
        assertThat(languages.getFirst().getName()).isEqualTo("English             ");
        assertThat(languages.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(1).getId()).isEqualTo(5);
        assertThat(languages.get(1).getName()).isEqualTo("French              ");
        assertThat(languages.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(2).getId()).isEqualTo(6);
        assertThat(languages.get(2).getName()).isEqualTo("German              ");
        assertThat(languages.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(3).getId()).isEqualTo(2);
        assertThat(languages.get(3).getName()).isEqualTo("Italian             ");
        assertThat(languages.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(4).getId()).isEqualTo(3);
        assertThat(languages.get(4).getName()).isEqualTo("Japanese            ");
        assertThat(languages.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(5).getId()).isEqualTo(4);
        assertThat(languages.get(5).getName()).isEqualTo("Mandarin            ");
        assertThat(languages.get(5).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageOnGetByName() {
        ResponseEntity<ResponsePageImpl<LanguageDto>> response = restTemplate.exchange(
                "/languages?name=an&page=0&size=5&sort=name,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(5);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(4);
        assertThat(response.getBody().getTotalElements()).isEqualTo(4);
        assertThat(response.getBody().getTotalPages()).isEqualTo(1);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isTrue();

        assertThat(response.getBody().getContent()).isNotNull();
        List<LanguageDto> languages = response.getBody().getContent();
        assertThat(languages.size()).isEqualTo(4);

        assertThat(languages.getFirst().getId()).isEqualTo(6);
        assertThat(languages.getFirst().getName()).isEqualTo("German              ");
        assertThat(languages.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(1).getId()).isEqualTo(2);
        assertThat(languages.get(1).getName()).isEqualTo("Italian             ");
        assertThat(languages.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(2).getId()).isEqualTo(3);
        assertThat(languages.get(2).getName()).isEqualTo("Japanese            ");
        assertThat(languages.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(3).getId()).isEqualTo(4);
        assertThat(languages.get(3).getName()).isEqualTo("Mandarin            ");
        assertThat(languages.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageWithDefaultPaginationOnGetByName() {
        ResponseEntity<ResponsePageImpl<LanguageDto>> response = restTemplate.exchange(
                "/languages?name=an",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(10);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(4);
        assertThat(response.getBody().getTotalElements()).isEqualTo(4);
        assertThat(response.getBody().getTotalPages()).isEqualTo(1);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isTrue();

        assertThat(response.getBody().getContent()).isNotNull();
        List<LanguageDto> languages = response.getBody().getContent();
        assertThat(languages.size()).isEqualTo(4);

        assertThat(languages.getFirst().getId()).isEqualTo(6);
        assertThat(languages.getFirst().getName()).isEqualTo("German              ");
        assertThat(languages.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(1).getId()).isEqualTo(2);
        assertThat(languages.get(1).getName()).isEqualTo("Italian             ");
        assertThat(languages.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(2).getId()).isEqualTo(3);
        assertThat(languages.get(2).getName()).isEqualTo("Japanese            ");
        assertThat(languages.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));

        assertThat(languages.get(3).getId()).isEqualTo(4);
        assertThat(languages.get(3).getName()).isEqualTo("Mandarin            ");
        assertThat(languages.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));
    }

    @Test
    @Override
    public void shouldReturnLanguageOnGetById() {
        ResponseEntity<LanguageDto> response = restTemplate.exchange(
                "/languages/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1);
        assertThat(response.getBody().getName()).isEqualTo("English             ");
        assertThat(response.getBody().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T10:02:19Z"));
    }

    @Test
    @Override
    public void shouldRejectNonExistingLanguageOnGetById() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/languages/99",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Language with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages/99");
    }

    @Test
    @Override
    @DirtiesContext
    public void shouldCreateNewLanguage() {
        LanguageDto languageDto = new LanguageDto(null, "Vietnamese", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/languages", entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/languages/8");

        ResponseEntity<LanguageDto> getResponse = restTemplate.getForEntity("/languages/8", LanguageDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(8);
        assertThat(getResponse.getBody().getName()).isEqualTo("Vietnamese          ");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    @Override
    public void shouldRejectLanguageWithInvalidNameOnCreate(String field, String invalidName, String message) {
        LanguageDto languageDto = new LanguageDto(null, invalidName, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/languages", entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Validation Failed");
        assertThat(response.getBody().getDetail()).isEqualTo("One or more fields are invalid. Check the properties for more details");
        assertThat(response.getBody().getProperties()).isNotNull();
        assertThat(response.getBody().getProperties().get("name")).isEqualTo(message);
    }

    @Test
    @Override
    public void shouldRejectLanguageWithExistingIdOnCreate() {
        LanguageDto languageDto = new LanguageDto(1, "Vietnamese", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/languages", entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Exists");
        assertThat(response.getBody().getDetail()).isEqualTo("Language with id 1 already exists");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldUpdateLanguage() {
        LanguageDto languageDto = new LanguageDto(4, "Chinese", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange("/languages/4", HttpMethod.PATCH, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<LanguageDto> getResponse = restTemplate.getForEntity("/languages/4", LanguageDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(4);
        assertThat(getResponse.getBody().getName()).isEqualTo("Chinese             ");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    @Override
    public void shouldRejectLanguageWithInvalidNameOnUpdate(String field, String invalidName, String message) {
        LanguageDto languageDto = new LanguageDto(null, invalidName, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/languages/4", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Validation Failed");
        assertThat(response.getBody().getDetail()).isEqualTo("One or more fields are invalid. Check the properties for more details");
        assertThat(response.getBody().getProperties()).isNotNull();
        assertThat(response.getBody().getProperties().get("name")).isEqualTo(message);
    }

    @Test
    @Override
    public void shouldRejectDifferentIdsOnUpdate() {
        LanguageDto languageDto = new LanguageDto(1, "Vietnamese", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/languages/4", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Language Ids do not match");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages/4");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldRejectNonExistingIdOnUpdate() {
        LanguageDto languageDto = new LanguageDto(99, "Vietnamese", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LanguageDto> entity = new HttpEntity<>(languageDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/languages/99", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Language with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages/99");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldDeleteLanguage() {
        ResponseEntity<Void> response = restTemplate.exchange("/languages/2", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ProblemDetail> getResponse = restTemplate.getForEntity("/languages/2", ProblemDetail.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getStatus()).isEqualTo(404);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(getResponse.getBody().getDetail()).isEqualTo("Language with id 2 not found");
        assertThat(getResponse.getBody().getInstance()).isNotNull();
        assertThat(getResponse.getBody().getInstance().toString()).isEqualTo("/languages/2");
    }

    @Test
    @Override
    public void shouldRejectNonExistingIdOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/languages/99", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Language with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages/99");
    }

    @Test
    @Override
    public void shouldRejectLanguageWithExistingForeignKeyOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/languages/1", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("One or more films are associated with the language with id 1");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/languages/1");
    }
}
