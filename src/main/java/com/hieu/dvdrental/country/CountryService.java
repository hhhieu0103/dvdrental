package com.hieu.dvdrental.country;

import com.hieu.dvdrental.city.CityRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CityRepository cityRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.cityRepository = cityRepository;
    }

    public CountryDto getCountryById(int id) {
        return countryRepository.findById(id)
                .map(countryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Country with id " + id + " not found"));
    }

    public Page<CountryDto> getAllCountries(Pageable pageable) {
        return countryRepository.findAll(pageable).map(countryMapper::toDto);
    }

    public Page<CountryDto> getCountriesByName(String name, Pageable pageable) {
        return countryRepository.findByNameContainingIgnoreCase(name, pageable).map(countryMapper::toDto);
    }

    public Integer addCountry(CountryDto countryDto) {
        if (countryDto.getId() != null) {
            if (countryRepository.existsById(countryDto.getId())) {
                throw new EntityExistsException("Country with id " + countryDto.getId() + " already exists");
            } else {
                countryDto.setId(null);
            }
        }
        return countryRepository.save(countryMapper.toEntity(countryDto)).getId();
    }

    public void updateCountry(CountryDto countryDto) {
        if (!countryRepository.existsById(countryDto.getId())) {
            throw new EntityNotFoundException("Country with id " + countryDto.getId() + " not found");
        }
        Country country = countryMapper.toEntity(countryDto);
        countryRepository.save(country);
    }

    public void deleteCountry(int id) {
        if (!countryRepository.existsById(id)) {
            throw new EntityNotFoundException("Country with id " + id + " not found");
        }
        if (cityRepository.existsByCountryId(id)) {
            throw new IllegalArgumentException("One or more cities are associated with the country with id " + id);
        }
        countryRepository.deleteById(id);
    }
}
