package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.CompetitionRequest;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.entity.CompetitionType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionRepository competitionRepository;

    public CompetitionController(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Competition getCompetitionById(@PathVariable Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Competition createCompetition(
            @Valid @RequestBody CompetitionRequest request
    ) {
        validateCompetitionSettings(request);

        Competition competition = new Competition(
                request.getName(),
                request.getType(),
                request.getGender(),
                request.getMinimumAge(),
                request.getNumberOfLaps(),
                request.getShootingAfterLaps()
        );

        return competitionRepository.save(competition);
    }

    @PutMapping("/{id}")
    public Competition updateCompetition(
            @PathVariable Long id,
            @Valid @RequestBody CompetitionRequest request
    ) {
        validateCompetitionSettings(request);

        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        competition.setName(request.getName());
        competition.setType(request.getType());
        competition.setGender(request.getGender());
        competition.setMinimumAge(request.getMinimumAge());
        competition.setNumberOfLaps(request.getNumberOfLaps());
        competition.setShootingAfterLaps(request.getShootingAfterLaps());

        return competitionRepository.save(competition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompetition(@PathVariable Long id) {
        competitionRepository.deleteById(id);
    }

    private void validateCompetitionSettings(CompetitionRequest request) {

        if (request.getType() == CompetitionType.BIATHLON) {

            if (request.getNumberOfLaps() == null) {
                throw new RuntimeException(
                        "Number of laps is required for biathlon"
                );
            }

            if (request.getShootingAfterLaps() == null) {
                throw new RuntimeException(
                        "Shooting lap is required for biathlon"
                );
            }

            if (request.getShootingAfterLaps() > request.getNumberOfLaps()) {
                throw new RuntimeException(
                        "Shooting lap cannot be greater than number of laps"
                );
            }

        } else if (request.getType() == CompetitionType.SKI_SLALOM) {

            if (request.getNumberOfLaps() != null ||
                    request.getShootingAfterLaps() != null) {

                throw new RuntimeException(
                        "Lap and shooting settings are only allowed for biathlon"
                );
            }
        }
    }
}