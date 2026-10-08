package com.example.winter_olympics.controller;

import com.example.winter_olympics.entity.Country;
import com.example.winter_olympics.repository.CountryRepository;
import com.example.winter_olympics.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Country not found"));
    }

    @PostMapping
    public Country createCountry(@RequestBody Country country) {
        return countryRepository.save(country);
    }

    @PutMapping("/{id}")
    public Country updateCountry(
            @PathVariable Long id,
            @RequestBody Country updatedCountry
    ) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Country not found"));

        country.setName(updatedCountry.getName());

        return countryRepository.save(country);
    }

    @DeleteMapping("/{id}")
    public void deleteCountry(@PathVariable Long id) {
        countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Country not found"));
        countryRepository.deleteById(id);
    }
}
