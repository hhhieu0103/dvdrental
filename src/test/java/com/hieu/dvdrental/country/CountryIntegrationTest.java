package com.hieu.dvdrental.country;

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
public class CountryIntegrationTest implements CountryTestSuit {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Override
    public void shouldReturnAPageOnGetAll() {
        ResponseEntity<ResponsePageImpl<CountryDto>> response = restTemplate.exchange(
                "/countries?page=0&size=5&sort=name,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(5);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(5);
        assertThat(response.getBody().getTotalElements()).isEqualTo(36);
        assertThat(response.getBody().getTotalPages()).isEqualTo(8);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<CountryDto> countries = response.getBody().getContent();
        assertThat(countries.size()).isEqualTo(5);

        assertThat(countries.getFirst().getId()).isEqualTo(6);
        assertThat(countries.getFirst().getName()).isEqualTo("Argentina");
        assertThat(countries.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(1).getId()).isEqualTo(7);
        assertThat(countries.get(1).getName()).isEqualTo("Armenia");
        assertThat(countries.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(2).getId()).isEqualTo(8);
        assertThat(countries.get(2).getName()).isEqualTo("Australia");
        assertThat(countries.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(3).getId()).isEqualTo(12);
        assertThat(countries.get(3).getName()).isEqualTo("Bangladesh");
        assertThat(countries.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(4).getId()).isEqualTo(15);
        assertThat(countries.get(4).getName()).isEqualTo("Brazil");
        assertThat(countries.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageWithDefaultPaginationOnGetAll() {
        ResponseEntity<ResponsePageImpl<CountryDto>> response = restTemplate.exchange(
                "/countries",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(10);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(10);
        assertThat(response.getBody().getTotalElements()).isEqualTo(36);
        assertThat(response.getBody().getTotalPages()).isEqualTo(4);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<CountryDto> countries = response.getBody().getContent();
        assertThat(countries.size()).isEqualTo(10);

        assertThat(countries.getFirst().getId()).isEqualTo(6);
        assertThat(countries.getFirst().getName()).isEqualTo("Argentina");
        assertThat(countries.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(1).getId()).isEqualTo(7);
        assertThat(countries.get(1).getName()).isEqualTo("Armenia");
        assertThat(countries.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(2).getId()).isEqualTo(8);
        assertThat(countries.get(2).getName()).isEqualTo("Australia");
        assertThat(countries.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(3).getId()).isEqualTo(12);
        assertThat(countries.get(3).getName()).isEqualTo("Bangladesh");
        assertThat(countries.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(4).getId()).isEqualTo(15);
        assertThat(countries.get(4).getName()).isEqualTo("Brazil");
        assertThat(countries.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(5).getId()).isEqualTo(20);
        assertThat(countries.get(5).getName()).isEqualTo("Canada");
        assertThat(countries.get(5).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(6).getId()).isEqualTo(22);
        assertThat(countries.get(6).getName()).isEqualTo("Chile");
        assertThat(countries.get(6).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(7).getId()).isEqualTo(23);
        assertThat(countries.get(7).getName()).isEqualTo("China");
        assertThat(countries.get(7).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(8).getId()).isEqualTo(34);
        assertThat(countries.get(8).getName()).isEqualTo("France");
        assertThat(countries.get(8).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(9).getId()).isEqualTo(36);
        assertThat(countries.get(9).getName()).isEqualTo("French Polynesia");
        assertThat(countries.get(9).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageOnGetByName() {
        ResponseEntity<ResponsePageImpl<CountryDto>> response = restTemplate.exchange(
                "/countries?name=an&page=0&size=5&sort=name,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(5);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(5);
        assertThat(response.getBody().getTotalElements()).isEqualTo(14);
        assertThat(response.getBody().getTotalPages()).isEqualTo(3);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<CountryDto> countries = response.getBody().getContent();
        assertThat(countries.size()).isEqualTo(5);

        assertThat(countries.getFirst().getId()).isEqualTo(12);
        assertThat(countries.getFirst().getName()).isEqualTo("Bangladesh");
        assertThat(countries.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(1).getId()).isEqualTo(20);
        assertThat(countries.get(1).getName()).isEqualTo("Canada");
        assertThat(countries.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(2).getId()).isEqualTo(34);
        assertThat(countries.get(2).getName()).isEqualTo("France");
        assertThat(countries.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(3).getId()).isEqualTo(41);
        assertThat(countries.get(3).getName()).isEqualTo("Holy See (Vatican City State)");
        assertThat(countries.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(4).getId()).isEqualTo(46);
        assertThat(countries.get(4).getName()).isEqualTo("Iran");
        assertThat(countries.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    @Override
    public void shouldReturnAPageWithDefaultPaginationOnGetByName() {
        ResponseEntity<ResponsePageImpl<CountryDto>> response = restTemplate.exchange(
                "/countries?name=an",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(10);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(10);
        assertThat(response.getBody().getTotalElements()).isEqualTo(14);
        assertThat(response.getBody().getTotalPages()).isEqualTo(2);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<CountryDto> countries = response.getBody().getContent();
        assertThat(countries.size()).isEqualTo(10);

        assertThat(countries.getFirst().getId()).isEqualTo(12);
        assertThat(countries.getFirst().getName()).isEqualTo("Bangladesh");
        assertThat(countries.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(1).getId()).isEqualTo(20);
        assertThat(countries.get(1).getName()).isEqualTo("Canada");
        assertThat(countries.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(2).getId()).isEqualTo(34);
        assertThat(countries.get(2).getName()).isEqualTo("France");
        assertThat(countries.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(3).getId()).isEqualTo(41);
        assertThat(countries.get(3).getName()).isEqualTo("Holy See (Vatican City State)");
        assertThat(countries.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(4).getId()).isEqualTo(46);
        assertThat(countries.get(4).getName()).isEqualTo("Iran");
        assertThat(countries.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(5).getId()).isEqualTo(50);
        assertThat(countries.get(5).getName()).isEqualTo("Japan");
        assertThat(countries.get(5).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(6).getId()).isEqualTo(64);
        assertThat(countries.get(6).getName()).isEqualTo("Myanmar");
        assertThat(countries.get(6).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(7).getId()).isEqualTo(68);
        assertThat(countries.get(7).getName()).isEqualTo("New Zealand");
        assertThat(countries.get(7).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(8).getId()).isEqualTo(71);
        assertThat(countries.get(8).getName()).isEqualTo("Oman");
        assertThat(countries.get(8).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(countries.get(9).getId()).isEqualTo(72);
        assertThat(countries.get(9).getName()).isEqualTo("Pakistan");
        assertThat(countries.get(9).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    @Override
    public void shouldReturnCountryOnGetById() {
        ResponseEntity<CountryDto> response = restTemplate.exchange(
                "/countries/6",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(6);
        assertThat(response.getBody().getName()).isEqualTo("Argentina");
        assertThat(response.getBody().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    @Override
    public void shouldRejectNonExistingCountryOnGetById() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/countries/99",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries/99");
    }

    @Test
    @Override
    @DirtiesContext
    public void shouldCreateNewCountry() {
        CountryDto countryDto = new CountryDto(null, "Vietnam", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/countries", entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/countries/110");

        ResponseEntity<CountryDto> getResponse = restTemplate.getForEntity("/countries/110", CountryDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(110);
        assertThat(getResponse.getBody().getName()).isEqualTo("Vietnam");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void shouldCreateNewCountryWithId() {
        CountryDto countryDto = new CountryDto(1, "Vietnam", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/countries", entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/countries/110");

        ResponseEntity<CountryDto> getResponse = restTemplate.getForEntity("/countries/110", CountryDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(110);
        assertThat(getResponse.getBody().getName()).isEqualTo("Vietnam");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    @Override
    public void shouldRejectCountryWithInvalidNameOnCreate(String field, String invalidName, String message) {
        CountryDto countryDto = new CountryDto(null, invalidName, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/countries", entity, ProblemDetail.class);
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
    public void shouldRejectCountryWithExistingIdOnCreate() {
        CountryDto countryDto = new CountryDto(36, "Vietnam", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/countries", entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Exists");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 36 already exists");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldUpdateCountry() {
        CountryDto countryDto = new CountryDto(103, "United States of America", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange("/countries/103", HttpMethod.PATCH, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<CountryDto> getResponse = restTemplate.getForEntity("/countries/103", CountryDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(103);
        assertThat(getResponse.getBody().getName()).isEqualTo("United States of America");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    @Override
    public void shouldRejectCountryWithInvalidNameOnUpdate(String field, String invalidName, String message) {
        CountryDto countryDto = new CountryDto(null, invalidName, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/countries/103", HttpMethod.PATCH, entity, ProblemDetail.class);
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
        CountryDto countryDto = new CountryDto(1, "United States of America", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/countries/103", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Country Ids do not match");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries/103");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldRejectNonExistingIdOnUpdate() {
        CountryDto countryDto = new CountryDto(99, "Vietnam", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/countries/99", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries/99");
    }

    @Test
    @DirtiesContext
    @Override
    public void shouldDeleteCountry() {
        CountryDto countryDto = new CountryDto(null, "Vietnam", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CountryDto> entity = new HttpEntity<>(countryDto, headers);

        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/countries", entity, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = restTemplate.exchange("/countries/110", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ProblemDetail> getResponse = restTemplate.getForEntity("/countries/110", ProblemDetail.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getStatus()).isEqualTo(404);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(getResponse.getBody().getDetail()).isEqualTo("Country with id 110 not found");
        assertThat(getResponse.getBody().getInstance()).isNotNull();
        assertThat(getResponse.getBody().getInstance().toString()).isEqualTo("/countries/110");
    }

    @Test
    @Override
    public void shouldRejectNonExistingIdOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/countries/99", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries/99");
    }

    @Test
    @Override
    public void shouldRejectCountryWithExistingForeignKeyOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/countries/103", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("One or more cities are associated with the country with id 103");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/countries/103");
    }
}
