package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.AthleteRequest;
import com.example.winter_olympics.dto.AthleteResponse;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Country;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.exception.ForbiddenException;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.CountryRepository;
import com.example.winter_olympics.repository.UserRepository;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;

    public AthleteController(
            AthleteRepository athleteRepository,
            CountryRepository countryRepository,
            UserRepository userRepository
    ) {
        this.athleteRepository = athleteRepository;
        this.countryRepository = countryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<AthleteResponse> getAll() {
        return athleteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AthleteResponse getById(@PathVariable Long id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found"));

        return toResponse(athlete);
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
            @Valid @RequestBody AthleteRequest request,
            Authentication authentication
    ) {
        checkOwnership(id, authentication);

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
    public void deleteAthlete(@PathVariable Long id, Authentication authentication) {
        checkOwnership(id, authentication);
        athleteRepository.deleteById(id);
    }

    private void checkOwnership(Long athleteId, Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAthlete() == null ||
                !user.getAthlete().getId().equals(athleteId)) {
            throw new ForbiddenException("You can only modify your own athlete data");
        }
    }

    private AthleteResponse toResponse(Athlete athlete) {
        return new AthleteResponse(
                athlete.getId(),
                athlete.getName(),
                athlete.getCountry().getId(),
                athlete.getCountry().getName(),
                athlete.getGender(),
                athlete.getDateOfBirth()
        );
    }
}