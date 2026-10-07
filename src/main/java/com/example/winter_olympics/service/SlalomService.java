package com.example.winter_olympics.service;

import com.example.winter_olympics.dto.SlalomRankingResponse;
import com.example.winter_olympics.dto.SlalomResultResponse;
import com.example.winter_olympics.entity.SlalomResult;
import com.example.winter_olympics.repository.SlalomResultRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class SlalomService {

    private final SlalomResultRepository resultRepository;

    public SlalomService(SlalomResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public SlalomResultResponse toResponse(SlalomResult result) {

        return new SlalomResultResponse(
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getName(),
                result.getRegistration().getAthlete().getCountry().getName(),
                result.getFirstRunTime(),
                result.isFirstRunFinished(),
                result.getSecondRunTime(),
                result.isSecondRunFinished()
        );
    }

    public BigDecimal calculateFinalTime(SlalomResult result) {

        if (!result.isFirstRunFinished()
                || !result.isSecondRunFinished()) {
            return null;
        }

        return result.getFirstRunTime()
                .add(result.getSecondRunTime());
    }

    public List<SlalomResult> getSecondRunParticipants(Long competitionId) {

        return resultRepository.findAll().stream()
                .filter(result ->
                        result.getRegistration()
                                .getCompetition()
                                .getId()
                                .equals(competitionId)
                )
                .filter(SlalomResult::isFirstRunFinished)
                .filter(result -> result.getFirstRunTime() != null)
                .sorted(Comparator.comparing(SlalomResult::getFirstRunTime))
                .limit(30)
                .toList();
    }

    public List<SlalomResult> getSecondRunStartOrder(Long competitionId) {

        List<SlalomResult> participants = getSecondRunParticipants(competitionId);

        return participants.stream()
                .sorted(Comparator.comparing(SlalomResult::getFirstRunTime).reversed())
                .toList();
    }

    public List<SlalomRankingResponse> getRanking(Long competitionId) {

        List<SlalomResult> results = resultRepository.findAll()
                .stream()
                .filter(result ->
                        result.getRegistration()
                                .getCompetition()
                                .getId()
                                .equals(competitionId)
                )
                .filter(SlalomResult::isFirstRunFinished)
                .filter(SlalomResult::isSecondRunFinished)
                .sorted(
                        Comparator.comparing(this::calculateFinalTime)
                )
                .toList();

        return java.util.stream.IntStream.range(0, results.size())
                .mapToObj(index -> {
                    SlalomResult result = results.get(index);

                    BigDecimal finalTime = calculateFinalTime(result);

                    return new SlalomRankingResponse(
                            index + 1,
                            result.getRegistration()
                                    .getAthlete()
                                    .getName(),
                            result.getRegistration()
                                    .getAthlete()
                                    .getCountry()
                                    .getName(),
                            result.getFirstRunTime(),
                            result.getSecondRunTime(),
                            finalTime
                    );
                })
                .toList();
    }
}