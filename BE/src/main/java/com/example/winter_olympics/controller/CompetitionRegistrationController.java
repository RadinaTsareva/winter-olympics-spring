package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.CompetitionRegistrationResponse;
import com.example.winter_olympics.dto.RegistrationRequest;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.exception.ForbiddenException;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.NotFoundException;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class CompetitionRegistrationController {

    private final CompetitionRegistrationRepository registrationRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;

    public CompetitionRegistrationController(
            CompetitionRegistrationRepository registrationRepository,
            AthleteRepository athleteRepository,
            CompetitionRepository competitionRepository,
            UserRepository userRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CompetitionRegistrationResponse> getAll(
            Authentication authentication
    ) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<CompetitionRegistration> registrations;

        if (user.getRole() == Role.ADMIN) {
            registrations = registrationRepository.findAll();
        } else {
            if (user.getAthlete() == null) {
                return List.of();
            }

            registrations = registrationRepository.findAll()
                    .stream()
                    .filter(registration ->
                            registration.getAthlete().getId()
                                    .equals(user.getAthlete().getId()))
                    .toList();
        }

        return registrations.stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompetitionRegistrationResponse create(
            @Valid @RequestBody RegistrationRequest request,
            Authentication authentication
    ) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new NotFoundException("Athlete not found"));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new NotFoundException("Competition not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAdmin) {
            if (user.getAthlete() == null ||
                    !user.getAthlete().getId().equals(athlete.getId())) {
                throw new ForbiddenException(
                        "You can only register yourself for competitions"
                );
            }
        }

        if (athlete.getGender() != competition.getGender()) {
            throw new BadRequestException(
                    "Athlete gender does not match competition gender"
            );
        }

        int age = Period.between(
                athlete.getDateOfBirth(),
                LocalDate.now()
        ).getYears();

        if (age < competition.getMinimumAge()) {
            throw new BadRequestException(
                    "Athlete does not meet the minimum age requirement"
            );
        }

        if (registrationRepository
                .existsByAthleteIdAndCompetitionId(
                        athlete.getId(),
                        competition.getId()
                )) {

            throw new BadRequestException(
                    "Athlete is already registered for this competition"
            );
        }

        CompetitionRegistration registration =
                new CompetitionRegistration(athlete, competition);

        CompetitionRegistration saved =
                registrationRepository.save(registration);

        return toResponse(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        CompetitionRegistration registration =
                registrationRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Registration not found"));

        checkOwnership(registration, authentication);

        registrationRepository.delete(registration);
    }

    private CompetitionRegistrationResponse toResponse(
            CompetitionRegistration registration
    ) {
        return new CompetitionRegistrationResponse(
                registration.getId(),
                registration.getAthlete().getId(),
                registration.getAthlete().getName(),
                registration.getCompetition().getId(),
                registration.getCompetition().getName()
        );
    }

    private void checkOwnership(
            CompetitionRegistration registration,
            Authentication authentication
    ) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (user.getAthlete() == null ||
                !user.getAthlete().getId()
                        .equals(registration.getAthlete().getId())) {

            throw new ForbiddenException(
                    "You can only manage your own registrations"
            );
        }
    }
}
