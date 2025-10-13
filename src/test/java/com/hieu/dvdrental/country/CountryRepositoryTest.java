package com.hieu.dvdrental.country;

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
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    @Test
    public void findByNameContainingIgnoreCase() {
        Page<Country> countryPage = countryRepository.findByNameContainingIgnoreCase("an", pageable);

        assertThat(countryPage.getNumber()).isEqualTo(0);
        assertThat(countryPage.getSize()).isEqualTo(5);
        assertThat(countryPage.getNumberOfElements()).isEqualTo(5);
        assertThat(countryPage.getTotalElements()).isEqualTo(14);
        assertThat(countryPage.getTotalPages()).isEqualTo(3);
        assertThat(countryPage.isFirst()).isTrue();
        assertThat(countryPage.isLast()).isFalse();

        assertThat(countryPage.getContent()).isNotNull();
        List<Country> countries = countryPage.getContent();

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
}
