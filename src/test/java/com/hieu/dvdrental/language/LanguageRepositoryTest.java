package com.hieu.dvdrental.language;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestContainersConfig.class)
public class LanguageRepositoryTest {
    @Autowired
    private LanguageRepository languageRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    @Test
    public void findByNameContainingIgnoreCase() {
        Page<Language> languages = languageRepository.findByNameContainingIgnoreCase("an", pageable);

        assertThat(languages.getContent()).hasSize(4);

        assertThat(languages.getContent().getFirst().getId()).isEqualTo(6);
        assertThat(languages.getContent().getFirst().getName()).isEqualTo("German              ");
        assertEquals(Instant.parse("2006-02-15T10:02:19Z"), languages.getContent().getFirst().getLastUpdate());

        assertThat(languages.getContent().get(1).getId()).isEqualTo(2);
        assertThat(languages.getContent().get(1).getName()).isEqualTo("Italian             ");
        assertEquals(Instant.parse("2006-02-15T10:02:19Z"), languages.getContent().get(1).getLastUpdate());

        assertThat(languages.getContent().get(2).getId()).isEqualTo(3);
        assertThat(languages.getContent().get(2).getName()).isEqualTo("Japanese            ");
        assertEquals(Instant.parse("2006-02-15T10:02:19Z"), languages.getContent().get(2).getLastUpdate());

        assertThat(languages.getContent().get(3).getId()).isEqualTo(4);
        assertThat(languages.getContent().get(3).getName()).isEqualTo("Mandarin            ");
        assertEquals(Instant.parse("2006-02-15T10:02:19Z"), languages.getContent().get(3).getLastUpdate());
    }
}
