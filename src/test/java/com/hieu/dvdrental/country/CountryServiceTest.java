package com.hieu.dvdrental.country;

import com.hieu.dvdrental.city.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CityRepository cityRepository;
    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);

    private CountryService countryService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    private final Instant updated = Instant.now();
    private final List<Country> countryEntityList = List.of(
            new Country(1, "France", updated),
            new Country(2, "Russian Federation", updated),
            new Country(3, "New Zealand", updated),
            new Country(4, "Bangladesh", updated),
            new Country(5, "Iran", updated)
    );

    private final List<CountryDto> countryDtoList = List.of(
            new CountryDto(1, "France", updated),
            new CountryDto(2, "Russian Federation", updated),
            new CountryDto(3, "New Zealand", updated),
            new CountryDto(4, "Bangladesh", updated),
            new CountryDto(5, "Iran", updated)
    );

    private final Page<Country> entityPage = new PageImpl<>(countryEntityList, pageable, countryEntityList.size());

    @BeforeEach
    void setUp() {
        this.countryService = new CountryService(countryRepository, countryMapper, cityRepository);
    }

    @Test
    public void shouldReturnACountryPage() {
        when(countryRepository.findAll(pageable)).thenReturn(entityPage);

        Page<CountryDto> countryPage = countryService.getAllCountries(pageable);

        assertThat(countryPage).isNotNull();
        assertThat(countryPage.getContent()).isNotNull();
        assertThat(countryPage.getContent().getFirst().getClass()).isEqualTo(CountryDto.class);

        assertThat(countryPage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(countryDtoList);
    }

    @Test
    public void shouldReturnCountryById() {
        when(countryRepository.findById(1)).thenReturn(Optional.of(countryEntityList.getFirst()));

        CountryDto countryDto = countryService.getCountryById(1);

        assertThat(countryDto).isNotNull();
        assertThat(countryDto.getClass()).isEqualTo(CountryDto.class);
        assertThat(countryDto.getId()).isEqualTo(1);
        assertThat(countryDto.getName()).isEqualTo("France");
        assertThat(countryDto.getLastUpdate()).isEqualTo(updated);
    }

    @Test
    public void shouldRejectOnFindByIdWhenIdIsNotExist() {
        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryById(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Country with id " + 1 + " not found");
    }

    @Test
    public void shouldReturnACountryPageByName() {
        when(countryRepository.findByNameContainingIgnoreCase("name", pageable)).thenReturn(entityPage);

        Page<CountryDto> countryPage = countryService.getCountriesByName("name", pageable);

        assertThat(countryPage).isNotNull();
        assertThat(countryPage.getContent()).isNotNull();
        assertThat(countryPage.getContent().getFirst().getClass()).isEqualTo(CountryDto.class);

        assertThat(countryPage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(countryDtoList);
    }

    @Test
    public void shouldCreateCountry() {
        when(countryRepository.save(any(Country.class))).thenReturn(countryEntityList.getFirst());

        Integer createdId = countryService.addCountry(countryDtoList.getFirst());
        assertThat(createdId).isNotNull();
        assertThat(createdId).isEqualTo(1);
    }

    @Test
    public void shouldUpdateCountry() {
        when(countryRepository.existsById(1)).thenReturn(true);

        countryService.updateCountry(countryDtoList.getFirst());

        verify(countryRepository).save(argThat(entity -> entity.getId() == 1 && entity.getName().equals("France")));
    }

    @Test
    public void shouldRejectOnUpdateWhenCountryIdIsNotExist() {
        when(countryRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> countryService.updateCountry(countryDtoList.getFirst()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Country with id " + 1 + " not found");
    }

    @Test
    public void shouldDeleteCountry() {
        when(countryRepository.existsById(1)).thenReturn(true);
        when(cityRepository.existsByCountryId(1)).thenReturn(false);

        countryService.deleteCountry(1);

        verify(countryRepository).deleteById(1);
    }

    @Test
    public void shouldRejectOnDeleteWhenCountryIdIsNotExist() {
        when(countryRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> countryService.deleteCountry(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Country with id " + 1 + " not found");
    }

    @Test
    public void shouldRejectOnDeleteWhenExistsCityWithCountryId() {
        when(countryRepository.existsById(1)).thenReturn(true);
        when(cityRepository.existsByCountryId(1)).thenReturn(true);

        assertThatThrownBy(() -> countryService.deleteCountry(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more cities are associated with the country with id 1");
    }
}
