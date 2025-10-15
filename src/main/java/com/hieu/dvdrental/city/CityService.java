package com.hieu.dvdrental.city;

import com.hieu.dvdrental.address.AddressRepository;
import com.hieu.dvdrental.country.CountryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public CityService(CityRepository cityRepository, CityMapper cityMapper, AddressRepository addressRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.addressRepository = addressRepository;
        this.countryRepository = countryRepository;
    }

    public CityDto getCityById(int id) {
        return cityRepository.findById(id)
                .map(cityMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("City with id " + id + " not found"));
    }

    public Page<CityDto> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(cityMapper::toDto);
    }

    public Page<CityDto> getCitiesByName(String name, Pageable pageable) {
        return cityRepository.findByNameContainingIgnoreCase(name, pageable).map(cityMapper::toDto);
    }

    public Page<CityDto> getCitiesByCountry(Integer countryId, Pageable pageable) {
        return cityRepository.findByCountryId(countryId, pageable).map(cityMapper::toDto);
    }

    public Integer addCity(CityDto cityDto) {
        if (!countryRepository.existsById(cityDto.getCountry().getId())) {
            throw new EntityNotFoundException("Country with id " + cityDto.getCountry().getId() + " not found");
        }
        cityDto.setId(null);
        City city = cityMapper.toEntity(cityDto);
        return cityRepository.save(city).getId();
    }

    public void updateCity(CityDto cityDto) {
        if (!cityRepository.existsById(cityDto.getId())) {
            throw new EntityNotFoundException("City with id " + cityDto.getId() + " not found");
        }
        if (!countryRepository.existsById(cityDto.getCountry().getId())) {
            throw new EntityNotFoundException("Country with id " + cityDto.getCountry().getId() + " not found");
        }
        City city = cityMapper.toEntity(cityDto);
        cityRepository.save(city);
    }

    public void deleteCity(int id) {
        if (!cityRepository.existsById(id)) {
            throw new EntityNotFoundException("City with id " + id + " not found");
        }
        if (addressRepository.existsByCityId(id)) {
            throw new IllegalArgumentException("One or more addresses are associated with the city with id " + id);
        }
        cityRepository.deleteById(id);
    }
}

