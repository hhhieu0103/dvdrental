package com.hieu.dvdrental.city;

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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestContainersConfig.class)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    @Test
    public void findByNameContainingIgnoreCase(){
        Page<City> cityPage = cityRepository.findByNameContainingIgnoreCase("am", pageable);

        assertThat(cityPage.getNumber()).isEqualTo(0);
        assertThat(cityPage.getSize()).isEqualTo(5);
        assertThat(cityPage.getNumberOfElements()).isEqualTo(5);
        assertThat(cityPage.getTotalElements()).isEqualTo(6);
        assertThat(cityPage.getTotalPages()).isEqualTo(2);
        assertThat(cityPage.isFirst()).isTrue();
        assertThat(cityPage.isLast()).isFalse();

        assertThat(cityPage.getContent()).isNotNull();
        List<City> cities = cityPage.getContent();

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
    public void existsByCountryId(){
        boolean exists = cityRepository.existsByCountryId(6);
        assertThat(exists).isTrue();

        boolean nonExists = cityRepository.existsByCountryId(1);
        assertThat(nonExists).isFalse();
    }

    @Test
    public void findByCountryId(){
        Page<City> cityPage = cityRepository.findByCountryId(50, pageable);

        assertThat(cityPage.getNumber()).isEqualTo(0);
        assertThat(cityPage.getSize()).isEqualTo(5);
        assertThat(cityPage.getNumberOfElements()).isEqualTo(5);
        assertThat(cityPage.getTotalElements()).isEqualTo(6);
        assertThat(cityPage.getTotalPages()).isEqualTo(2);
        assertThat(cityPage.isFirst()).isTrue();
        assertThat(cityPage.isLast()).isFalse();

        assertThat(cityPage.getContent()).isNotNull();
        List<City> cities = cityPage.getContent();

        assertThat(cities.getFirst().getId()).isEqualTo(227);
        assertThat(cities.getFirst().getName()).isEqualTo("Iwakuni");
        assertThat(cities.getFirst().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.getFirst().getCountry().getId()).isEqualTo(50);
        assertThat(cities.getFirst().getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.getFirst().getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(1).getId()).isEqualTo(284);
        assertThat(cities.get(1).getName()).isEqualTo("Kurashiki");
        assertThat(cities.get(1).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(1).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(1).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(1).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(2).getId()).isEqualTo(355);
        assertThat(cities.get(2).getName()).isEqualTo("Nagareyama");
        assertThat(cities.get(2).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(2).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(2).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(2).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(3).getId()).isEqualTo(440);
        assertThat(cities.get(3).getName()).isEqualTo("Sagamihara");
        assertThat(cities.get(3).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(3).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(3).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(3).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));

        assertThat(cities.get(4).getId()).isEqualTo(463);
        assertThat(cities.get(4).getName()).isEqualTo("Sasebo");
        assertThat(cities.get(4).getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:45:25Z"));
        assertThat(cities.get(4).getCountry().getId()).isEqualTo(50);
        assertThat(cities.get(4).getCountry().getName()).isEqualTo("Japan");
        assertThat(cities.get(4).getCountry().getLastUpdate()).isEqualTo(Instant.parse("2006-02-15T09:44:00Z"));


    }
}
