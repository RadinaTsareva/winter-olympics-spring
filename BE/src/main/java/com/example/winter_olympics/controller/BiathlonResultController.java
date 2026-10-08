package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.BiathlonResultRequest;
import com.example.winter_olympics.dto.BiathlonResultResponse;
import com.example.winter_olympics.dto.BiathlonRankingResponse;
import com.example.winter_olympics.entity.BiathlonResult;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.entity.CompetitionType;
import com.example.winter_olympics.repository.BiathlonResultRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.service.BiathlonService;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biathlon-results")
public class BiathlonResultController {

    private final BiathlonResultRepository resultRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final BiathlonService biathlonService;

    public BiathlonResultController(
            BiathlonResultRepository resultRepository,
            CompetitionRegistrationRepository registrationRepository,
            BiathlonService biathlonService
    ) {
        this.resultRepository = resultRepository;
        this.registrationRepository = registrationRepository;
        this.biathlonService = biathlonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BiathlonResultResponse saveResult(
            @Valid @RequestBody BiathlonResultRequest request
    ) {
        CompetitionRegistration registration =
                registrationRepository.findById(request.getRegistrationId())
                        .orElseThrow(() ->
                                new NotFoundException("Registration not found"));

        if (registration.getCompetition().getType() != CompetitionType.BIATHLON) {
            throw new BadRequestException(
                    "Registration is not for a biathlon competition"
            );
        }

        Competition competition = registration.getCompetition();

        if (competition.getNumberOfLaps() == null ||
                competition.getShootingAfterLaps() == null) {

            throw new BadRequestException(
                    "Biathlon competition is missing lap or shooting settings"
            );
        }

        BiathlonResult result = resultRepository.findAll()
                .stream()
                .filter(r -> r.getRegistration().getId()
                        .equals(registration.getId()))
                .findFirst()
                .orElseGet(() ->
                        new BiathlonResult(
                                registration,
                                request.getPenaltyPerMiss()
                        )
                );

        result.setSkiTime(request.getSkiTime());
        result.setMisses(request.getMisses());
        result.setPenaltyPerMiss(request.getPenaltyPerMiss());
        result.setFinished(request.getFinished());

        BiathlonResult savedResult = resultRepository.save(result);

        return biathlonService.toResponse(savedResult);
    }

    @GetMapping("/ranking/{competitionId}")
    public List<BiathlonRankingResponse> getRanking(
            @PathVariable Long competitionId
    ) {
        return biathlonService.getRankingResponse(competitionId);
    }
}
