package com.hieu.dvdrental.city;

import com.hieu.dvdrental.address.AddressRepository;
import com.hieu.dvdrental.country.CountryRepository;
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

import static com.hieu.dvdrental.country.CountryServiceTest.countryDtoList;
import static com.hieu.dvdrental.country.CountryServiceTest.countryEntityList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CountryRepository countryRepository;

    private final CityMapper cityMapper = Mappers.getMapper(CityMapper.class);

    private CityService cityService;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

    private final Instant updated = Instant.now();

    private final List<City> cityEntityList = List.of(
            new City(1, "France", countryEntityList.getFirst(), updated),
            new City(2, "Russian Federation", countryEntityList.get(1), updated),
            new City(3, "New Zealand", countryEntityList.get(2), updated),
            new City(4, "Bangladesh", countryEntityList.get(3), updated),
            new City(5, "Iran", countryEntityList.get(4), updated)
    );

    private final List<CityDto> cityDtoList = List.of(
            new CityDto(1, "France", updated, countryDtoList.getFirst()),
            new CityDto(2, "Russian Federation", updated, countryDtoList.get(1)),
            new CityDto(3, "New Zealand", updated, countryDtoList.get(2)),
            new CityDto(4, "Bangladesh", updated, countryDtoList.get(3)),
            new CityDto(5, "Iran", updated, countryDtoList.get(4))
    );

    private final Page<City> entityPage = new PageImpl<>(cityEntityList, pageable, cityEntityList.size());

    @BeforeEach
    public void setUp() {
        this.cityService = new CityService(cityRepository, cityMapper, addressRepository, countryRepository);
    }

    @Test
    public void shouldReturnACityPage() {
        when(cityRepository.findAll(pageable)).thenReturn(entityPage);

        Page<CityDto> cityPage = cityService.getAllCities(pageable);

        assertThat(cityPage).isNotNull();
        assertThat(cityPage.getContent()).isNotNull();
        assertThat(cityPage.getContent().getFirst().getClass()).isEqualTo(CityDto.class);

        assertThat(cityPage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(cityDtoList);

        verify(cityRepository).findAll(pageable);
    }

    @Test
    public void shouldReturnCityById() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(cityEntityList.getFirst()));

        CityDto cityDto = cityService.getCityById(1);

        assertThat(cityDto).isNotNull();
        assertThat(cityDto.getClass()).isEqualTo(CityDto.class);
        assertThat(cityDto.getId()).isEqualTo(1);
        assertThat(cityDto.getName()).isEqualTo("France");
        assertThat(cityDto.getLastUpdate()).isEqualTo(updated);

        verify(cityRepository).findById(1);
    }

    @Test
    public void shouldRejectOnFindByIdWhenIdIsNotExist() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.getCityById(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("City with id " + 1 + " not found");

        verify(cityRepository).findById(1);
    }

    @Test
    public void shouldReturnACityPageByName() {
        when(cityRepository.findByNameContainingIgnoreCase("name", pageable)).thenReturn(entityPage);

        Page<CityDto> cityPage = cityService.getCitiesByName("name", pageable);

        assertThat(cityPage).isNotNull();
        assertThat(cityPage.getContent()).isNotNull();
        assertThat(cityPage.getContent().getFirst().getClass()).isEqualTo(CityDto.class);

        assertThat(cityPage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(cityDtoList);

        verify(cityRepository).findByNameContainingIgnoreCase("name", pageable);
    }

    @Test
    public void shouldReturnACityPageByCountryId() {
        when(cityRepository.findByCountryId(50, pageable)).thenReturn(entityPage);

        Page<CityDto> cityPage = cityService.getCitiesByCountry(50, pageable);

        assertThat(cityPage).isNotNull();
        assertThat(cityPage.getContent()).isNotNull();
        assertThat(cityPage.getContent().getFirst().getClass()).isEqualTo(CityDto.class);

        assertThat(cityPage.getContent())
                .usingRecursiveComparison()
                .isEqualTo(cityDtoList);

        verify(cityRepository).findByCountryId(50, pageable);
    }

    @Test
    public void shouldCreateCountry() {
        when(countryRepository.existsById(1)).thenReturn(true);
        when(cityRepository.save(any(City.class))).thenReturn(cityEntityList.getFirst());

        Integer createdId = cityService.addCity(cityDtoList.getFirst());
        assertThat(createdId).isNotNull();
        assertThat(createdId).isEqualTo(1);

        verify(cityRepository).save(any(City.class));
    }

    @Test
    public void shouldRejectNotExistingCountryOnCreate() {
        when(countryRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> cityService.addCity(cityDtoList.getFirst()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Country with id 1 not found");

        verify(countryRepository).existsById(1);
    }

    @Test
    public void shouldUpdateCountry() {
        when(cityRepository.existsById(1)).thenReturn(true);
        when(countryRepository.existsById(1)).thenReturn(true);

        cityService.updateCity(cityDtoList.getFirst());

        verify(cityRepository).save(argThat(entity -> entity.getId() == 1 && entity.getName().equals("France")));
    }

    @Test
    public void shouldRejectOnUpdateWhenCountryIdIsNotExist() {
        when(cityRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> cityService.updateCity(cityDtoList.getFirst()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("City with id " + 1 + " not found");

        verify(cityRepository).existsById(1);
    }

    @Test
    public void shouldRejectNotExistingCountryOnUpdate() {
        when(cityRepository.existsById(1)).thenReturn(true);
        when(countryRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> cityService.updateCity(cityDtoList.getFirst()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Country with id 1 not found");

        verify(cityRepository).existsById(1);
        verify(countryRepository).existsById(1);
    }

    @Test
    public void shouldDeleteCountry() {
        when(cityRepository.existsById(1)).thenReturn(true);
        when(addressRepository.existsByCityId(1)).thenReturn(false);

        cityService.deleteCity(1);

        verify(cityRepository).deleteById(1);
        verify(addressRepository).existsByCityId(1);
    }

    @Test
    public void shouldRejectOnDeleteWhenCountryIdIsNotExist() {
        when(cityRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> cityService.deleteCity(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("City with id " + 1 + " not found");

        verify(cityRepository).existsById(1);
    }

    @Test
    public void shouldRejectOnDeleteWhenExistsCityWithCountryId() {
        when(cityRepository.existsById(1)).thenReturn(true);
        when(addressRepository.existsByCityId(1)).thenReturn(true);

        assertThatThrownBy(() -> cityService.deleteCity(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more addresses are associated with the city with id 1");

        verify(cityRepository).existsById(1);
        verify(addressRepository).existsByCityId(1);
    }
}
