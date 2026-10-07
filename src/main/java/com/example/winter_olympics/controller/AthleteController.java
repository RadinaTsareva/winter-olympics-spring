package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.AthleteRequest;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Country;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.CountryRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;

    public AthleteController(
            AthleteRepository athleteRepository,
            CountryRepository countryRepository
    ) {
        this.athleteRepository = athleteRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public List<Athlete> getAllAthletes() {
        return athleteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Athlete getAthleteById(@PathVariable Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Athlete createAthlete(@Valid @RequestBody AthleteRequest request) {

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        Athlete athlete = new Athlete(
                request.getName(),
                country,
                request.getGender(),
                request.getDateOfBirth()
        );

        return athleteRepository.save(athlete);
    }

    @PutMapping("/{id}")
    public Athlete updateAthlete(
            @PathVariable Long id,
            @Valid @RequestBody AthleteRequest request
    ) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found"));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        athlete.setName(request.getName());
        athlete.setCountry(country);
        athlete.setGender(request.getGender());
        athlete.setDateOfBirth(request.getDateOfBirth());

        return athleteRepository.save(athlete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAthlete(@PathVariable Long id) {
        athleteRepository.deleteById(id);
    }
}