package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.SlalomResultRequest;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.entity.SlalomResult;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.SlalomResultRepository;
import com.example.winter_olympics.service.SlalomService;
import com.example.winter_olympics.dto.SlalomRankingResponse;
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
                                new RuntimeException("Registration not found"));

        SlalomResult result = resultRepository.findAll()
                .stream()
                .filter(r -> r.getRegistration().getId()
                        .equals(registration.getId()))
                .findFirst()
                .orElseGet(() -> new SlalomResult(registration));

        result.setFirstRunTime(request.getTime());
        result.setFirstRunFinished(request.getFinished());

        return resultRepository.save(result);
    }

    @PostMapping("/second-run")
    public SlalomResult saveSecondRun(
            @Valid @RequestBody SlalomResultRequest request
    ) {
        CompetitionRegistration registration =
                registrationRepository.findById(request.getRegistrationId())
                        .orElseThrow(() ->
                                new RuntimeException("Registration not found"));

        SlalomResult result = resultRepository.findAll()
                .stream()
                .filter(r -> r.getRegistration().getId()
                        .equals(registration.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("First run result not found"));

        result.setSecondRunTime(request.getTime());
        result.setSecondRunFinished(request.getFinished());

        return resultRepository.save(result);
    }

    @GetMapping("/ranking/{competitionId}")
    public List<SlalomRankingResponse> getRanking(
            @PathVariable Long competitionId
    ) {
        return slalomService.getRanking(competitionId);
    }
}