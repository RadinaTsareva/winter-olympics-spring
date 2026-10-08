package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.SlalomResultRequest;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.entity.CompetitionType;
import com.example.winter_olympics.entity.SlalomResult;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.SlalomResultRepository;
import com.example.winter_olympics.service.SlalomService;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.NotFoundException;
import com.example.winter_olympics.dto.SlalomRankingResponse;
import com.example.winter_olympics.dto.SlalomResultResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/slalom-results")
public class SlalomResultController {

    private final SlalomResultRepository resultRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final SlalomService slalomService;

    public SlalomResultController(
            SlalomResultRepository resultRepository,
            CompetitionRegistrationRepository registrationRepository,
            SlalomService slalomService
    ) {
        this.resultRepository = resultRepository;
        this.registrationRepository = registrationRepository;
        this.slalomService = slalomService;
    }

    @PostMapping("/first-run")
    @ResponseStatus(HttpStatus.CREATED)
    public SlalomResult saveFirstRun(
            @Valid @RequestBody SlalomResultRequest request
    ) {
        CompetitionRegistration registration =
                registrationRepository.findById(request.getRegistrationId())
                        .orElseThrow(() ->
                                new NotFoundException("Registration not found"));

        if (registration.getCompetition().getType() != CompetitionType.SKI_SLALOM) {
            throw new BadRequestException(
                    "Registration is not for a ski slalom competition"
            );
        }

        SlalomResult result = resultRepository.findAll()
                .stream()
                .filter(r -> r.getRegistration().getId()
                        .equals(registration.getId()))
                .findFirst()
                .orElseGet(() -> new SlalomResult(registration));

        result.setFirstRunTime(request.getTime());
        result.setFirstRunFinished(request.getFinished());

        result.setSecondRunTime(null);
        result.setSecondRunFinished(false);

        if (!request.getFinished()) {
            result.setSecondRunTime(null);
            result.setSecondRunFinished(false);
        }

        return resultRepository.save(result);
    }

    @PostMapping("/second-run")
    public SlalomResult saveSecondRun(
            @Valid @RequestBody SlalomResultRequest request
    ) {
        CompetitionRegistration registration =
                registrationRepository.findById(request.getRegistrationId())
                        .orElseThrow(() ->
                                new NotFoundException("Registration not found"));

        if (registration.getCompetition().getType() != CompetitionType.SKI_SLALOM) {
            throw new BadRequestException(
                    "Registration is not for a ski slalom competition"
            );
        }

        SlalomResult result = resultRepository.findAll()
                .stream()
                .filter(r -> r.getRegistration().getId()
                        .equals(registration.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("First run result not found"));

        if (!result.isFirstRunFinished()) {
            throw new BadRequestException(
                    "Athlete did not finish the first run"
            );
        }

        List<SlalomResult> secondRunParticipants =
                slalomService.getSecondRunParticipants(
                        registration.getCompetition().getId()
                );

        boolean qualified = secondRunParticipants.stream()
                .anyMatch(r ->
                        r.getRegistration().getId()
                                .equals(registration.getId())
                );

        if (!qualified) {
            throw new BadRequestException(
                    "Athlete is not qualified for the second run"
            );
        }

        result.setSecondRunTime(request.getTime());
        result.setSecondRunFinished(request.getFinished());

        return resultRepository.save(result);
    }

    @GetMapping("/second-run/participants/{competitionId}")
    public List<SlalomResultResponse> getSecondRunParticipants(
            @PathVariable Long competitionId) {

        return slalomService.getSecondRunParticipants(competitionId)
                .stream()
                .map(slalomService::toResponse)
                .toList();
    }

    @GetMapping("/second-run/start-order/{competitionId}")
    public List<SlalomResultResponse> getSecondRunStartOrder(
            @PathVariable Long competitionId) {

        return slalomService.getSecondRunStartOrder(competitionId)
                .stream()
                .map(slalomService::toResponse)
                .toList();
    }

    @GetMapping("/ranking/{competitionId}")
    public List<SlalomRankingResponse> getRanking(
            @PathVariable Long competitionId
    ) {
        return slalomService.getRanking(competitionId);
    }
}
