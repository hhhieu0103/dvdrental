package com.hieu.dvdrental.city;

import com.hieu.dvdrental.ResponsePageImpl;
import com.hieu.dvdrental.config.TestContainersConfig;
import com.hieu.dvdrental.country.CountryDto;
import org.junit.jupiter.api.Test;
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
public class CityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnAPageOnGetAll() {
        ResponseEntity<ResponsePageImpl<CityDto>> response = restTemplate.exchange(
                "/cities?page=0&size=5&sort=name,asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getNumber()).isEqualTo(0);
        assertThat(response.getBody().getSize()).isEqualTo(5);
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(5);
        assertThat(response.getBody().getTotalElements()).isEqualTo(58);
        assertThat(response.getBody().getTotalPages()).isEqualTo(12);
        assertThat(response.getBody().isFirst()).isTrue();
        assertThat(response.getBody().isLast()).isFalse();

        assertThat(response.getBody().getContent()).isNotNull();
        List<CityDto> cities = response.getBody().getContent();
        assertThat(cities.size()).isEqualTo(5);

        assertThat(cities.getFirst().getId()).isEqualTo(1);
        assertThat(cities.getFirst().getName()).isEqualTo("A Corua (La Corua)");
        assertThat(cities.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.getFirst().getCountry().getId()).isEqualTo(87);
        assertThat(cities.getFirst().getCountry().getName()).isEqualTo("Spain");
        assertThat(cities.getFirst().getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(1).getId()).isEqualTo(17);
        assertThat(cities.get(1).getName()).isEqualTo("Alessandria");
        assertThat(cities.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(1).getCountry().getId()).isEqualTo(49);
        assertThat(cities.get(1).getCountry().getName()).isEqualTo("Italy");
        assertThat(cities.get(1).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(2).getId()).isEqualTo(27);
        assertThat(cities.get(2).getName()).isEqualTo("Antofagasta");
        assertThat(cities.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(2).getCountry().getId()).isEqualTo(22);
        assertThat(cities.get(2).getCountry().getName()).isEqualTo("Chile");
        assertThat(cities.get(2).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(3).getId()).isEqualTo(31);
        assertThat(cities.get(3).getName()).isEqualTo("Arak");
        assertThat(cities.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(3).getCountry().getId()).isEqualTo(46);
        assertThat(cities.get(3).getCountry().getName()).isEqualTo("Iran");
        assertThat(cities.get(3).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(4).getId()).isEqualTo(38);
        assertThat(cities.get(4).getName()).isEqualTo("Athenai");
        assertThat(cities.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(4).getCountry().getId()).isEqualTo(39);
        assertThat(cities.get(4).getCountry().getName()).isEqualTo("Greece");
        assertThat(cities.get(4).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    public void shouldReturnAPageOnGetByName() {
        ResponseEntity<ResponsePageImpl<CityDto>> response = restTemplate.exchange(
                "/cities?name=am&page=0&size=5&sort=name,asc",
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
        List<CityDto> cities = response.getBody().getContent();
        assertThat(cities.size()).isEqualTo(5);

        assertThat(cities.getFirst().getId()).isEqualTo(200);
        assertThat(cities.getFirst().getName()).isEqualTo("Hamilton");
        assertThat(cities.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.getFirst().getCountry().getId()).isEqualTo(68);
        assertThat(cities.getFirst().getCountry().getName()).isEqualTo("New Zealand");
        assertThat(cities.getFirst().getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(1).getId()).isEqualTo(257);
        assertThat(cities.get(1).getName()).isEqualTo("Kamarhati");
        assertThat(cities.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(1).getCountry().getId()).isEqualTo(44);
        assertThat(cities.get(1).getCountry().getName()).isEqualTo("India");
        assertThat(cities.get(1).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(2).getId()).isEqualTo(355);
        assertThat(cities.get(2).getName()).isEqualTo("Nagareyama");
        assertThat(cities.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(2).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(2).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(2).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(3).getId()).isEqualTo(367);
        assertThat(cities.get(3).getName()).isEqualTo("Niznekamsk");
        assertThat(cities.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(3).getCountry().getId()).isEqualTo(80);
        assertThat(cities.get(3).getCountry().getName()).isEqualTo("Russian Federation");
        assertThat(cities.get(3).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(4).getId()).isEqualTo(440);
        assertThat(cities.get(4).getName()).isEqualTo("Sagamihara");
        assertThat(cities.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(4).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(4).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(4).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    public void shouldReturnCityOnGetById() {
        ResponseEntity<CityDto> response = restTemplate.exchange(
                "/cities/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1);
        assertThat(response.getBody().getName()).isEqualTo("A Corua (La Corua)");
        assertThat(response.getBody().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(response.getBody().getCountry().getId()).isEqualTo(87);
        assertThat(response.getBody().getCountry().getName()).isEqualTo("Spain");
        assertThat(response.getBody().getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));
    }

    @Test
    public void shouldRejectNonExistingCityOnGetById() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/cities/99",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("City with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities/99");
    }

    @Test
    @DirtiesContext
    public void shouldCreateNewCity() {
        CityDto cityDto = new CityDto(null, "Hiroshima", null, new CountryDto(50));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/cities", entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/cities/588");

        ResponseEntity<CityDto> getResponse = restTemplate.getForEntity("/cities/588", CityDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(588);
        assertThat(getResponse.getBody().getName()).isEqualTo("Hiroshima");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
        assertThat(getResponse.getBody().getCountry().getId()).isEqualTo(50);
        assertThat(getResponse.getBody().getCountry().getName()).isEqualTo("Japan");
        assertThat(getResponse.getBody().getCountry().getLastUpdate()).isNotNull();
    }

    @Test
    public void shouldRejectNotExistingCountryOnCreate() {
        CityDto cityDto = new CityDto(null, "Hiroshima", null, new CountryDto(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/cities", entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 1 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities");
    }

    @Test
    @DirtiesContext
    public void shouldUpdateCity() {
        CityDto cityDto = new CityDto(1, "Hiroshima", null, new CountryDto(50));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange("/cities/1", HttpMethod.PATCH, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<CityDto> getResponse = restTemplate.getForEntity("/cities/1", CityDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(1);
        assertThat(getResponse.getBody().getName()).isEqualTo("Hiroshima");
        assertThat(getResponse.getBody().getLastUpdate()).isNotNull();
        assertThat(getResponse.getBody().getCountry().getId()).isEqualTo(50);
        assertThat(getResponse.getBody().getCountry().getName()).isEqualTo("Japan");
        assertThat(getResponse.getBody().getCountry().getLastUpdate()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void shouldRejectNonExistingIdOnUpdate() {
        CityDto cityDto = new CityDto(99, "Vietnam", null, new CountryDto(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/cities/99", HttpMethod.PATCH, entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("City with id 99 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities/99");
    }

    @Test
    public void shouldRejectNotExistingCountryOnUpdate() {
        CityDto cityDto = new CityDto(1, "Hiroshima", null, new CountryDto(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/cities", entity, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Country with id 1 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities");
    }

    @Test
    @DirtiesContext
    public void shouldDeleteCity() {
        CityDto cityDto = new CityDto(null, "Ho Chi Minh", null, new CountryDto(6));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CityDto> entity = new HttpEntity<>(cityDto, headers);

        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cities", entity, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = restTemplate.exchange("/cities/588", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ProblemDetail> getResponse = restTemplate.getForEntity("/cities/588", ProblemDetail.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getStatus()).isEqualTo(404);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(getResponse.getBody().getDetail()).isEqualTo("City with id 588 not found");
        assertThat(getResponse.getBody().getInstance()).isNotNull();
        assertThat(getResponse.getBody().getInstance().toString()).isEqualTo("/cities/588");
    }

    @Test
    public void shouldRejectNonExistingIdOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/cities/2", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Entity Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("City with id 2 not found");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities/2");
    }

    @Test
    public void shouldRejectCityWithExistingForeignKeyOnDelete() {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange("/cities/1", HttpMethod.DELETE, null, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("One or more addresses are associated with the city with id 1");
        assertThat(response.getBody().getInstance()).isNotNull();
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/cities/1");
    }
}
