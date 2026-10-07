package com.example.winter_olympics.service;

import com.example.winter_olympics.entity.BiathlonResult;
import com.example.winter_olympics.repository.BiathlonResultRepository;
import com.example.winter_olympics.dto.BiathlonResultResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class BiathlonService {

    private final BiathlonResultRepository resultRepository;

    public BiathlonService(BiathlonResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public BigDecimal calculatePenalty(BiathlonResult result) {

        return result.getPenaltyPerMiss()
                .multiply(BigDecimal.valueOf(result.getMisses()));
    }

    public BigDecimal calculateFinalTime(BiathlonResult result) {

        if (!result.isFinished()) {
            return null;
        }

        BigDecimal penalty = calculatePenalty(result);

        return result.getSkiTime().add(penalty);
    }

    public BiathlonResultResponse toResponse(BiathlonResult result) {

        BigDecimal penaltyTime = calculatePenalty(result);
        BigDecimal finalTime = calculateFinalTime(result);

        return new BiathlonResultResponse(
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getName(),
                result.getRegistration().getAthlete().getCountry().getName(),
                result.getSkiTime(),
                result.getMisses(),
                result.getPenaltyPerMiss(),
                penaltyTime,
                finalTime,
                result.isFinished()
        );
    }

    public List<BiathlonResult> getRanking(Long competitionId) {

        return resultRepository.findAll().stream()
                .filter(result ->
                        result.getRegistration()
                                .getCompetition()
                                .getId()
                                .equals(competitionId)
                )
                .filter(BiathlonResult::isFinished)
                .filter(result -> result.getSkiTime() != null)
                .sorted(Comparator.comparing(this::calculateFinalTime))
                .toList();
    }
}