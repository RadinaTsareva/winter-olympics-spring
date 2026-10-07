package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.RegistrationRequest;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.CompetitionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class CompetitionRegistrationController {

    private final CompetitionRegistrationRepository registrationRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;

    public CompetitionRegistrationController(
            CompetitionRegistrationRepository registrationRepository,
            AthleteRepository athleteRepository,
            CompetitionRepository competitionRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
    }

    @GetMapping
    public List<CompetitionRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompetitionRegistration register(
            @Valid @RequestBody RegistrationRequest request
    ) {
        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new RuntimeException("Athlete not found"));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        if (athlete.getGender() != competition.getGender()) {
            throw new RuntimeException(
                    "Athlete gender does not match competition gender"
            );
        }

        int athleteAge = java.time.Period
                .between(
                        athlete.getDateOfBirth(),
                        java.time.LocalDate.now()
                )
                .getYears();

        if (athleteAge < competition.getMinimumAge()) {
            throw new RuntimeException(
                    "Athlete does not meet the minimum age requirement"
            );
        }

        CompetitionRegistration registration =
                new CompetitionRegistration(athlete, competition);

        return registrationRepository.save(registration);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable Long id) {
        registrationRepository.deleteById(id);
    }
}