package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.CountryMedalResponse;
import com.example.winter_olympics.dto.MedalResponse;
import com.example.winter_olympics.dto.OlympicStatisticsResponse;
import com.example.winter_olympics.entity.Medal;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.service.OlympicStatisticsService;
import com.example.winter_olympics.service.SlalomService;
import com.example.winter_olympics.service.BiathlonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/olympics")
public class OlympicController {

    private final OlympicStatisticsService statisticsService;
    private final SlalomService slalomService;
    private final BiathlonService biathlonService;
    private final CompetitionRepository competitionRepository;

    public OlympicController(
            OlympicStatisticsService statisticsService,
            SlalomService slalomService,
            BiathlonService biathlonService,
            CompetitionRepository competitionRepository
    ) {
        this.statisticsService = statisticsService;
        this.slalomService = slalomService;
        this.biathlonService = biathlonService;
        this.competitionRepository = competitionRepository;
    }

    @GetMapping("/medals/slalom/{competitionId}")
    public List<MedalResponse> getSlalomMedals(
            @PathVariable Long competitionId
    ) {
        return slalomService.getRanking(competitionId)
                .stream()
                .limit(3)
                .map(ranking -> new MedalResponse(
                        ranking.getPosition(),
                        ranking.getAthleteName(),
                        ranking.getCountry(),
                        statisticsService.getMedalForPosition(
                                ranking.getPosition()
                        )
                ))
                .toList();
    }

    @GetMapping("/medals/biathlon/{competitionId}")
    public List<MedalResponse> getBiathlonMedals(
            @PathVariable Long competitionId
    ) {
        return biathlonService.getRankingResponse(competitionId)
                .stream()
                .limit(3)
                .map(ranking -> new MedalResponse(
                        ranking.getPosition(),
                        ranking.getAthleteName(),
                        ranking.getCountry(),
                        statisticsService.getMedalForPosition(
                                ranking.getPosition()
                        )
                ))
                .toList();
    }

    @GetMapping("/medals")
    public List<CountryMedalResponse> getCountryMedals() {

        List<MedalResponse> allMedals = new java.util.ArrayList<>();

        competitionRepository.findAll().forEach(competition -> {

            if (competition.getType() ==
                    com.example.winter_olympics.entity.CompetitionType.SKI_SLALOM) {

                allMedals.addAll(
                        getSlalomMedals(competition.getId())
                );

            } else if (competition.getType() ==
                    com.example.winter_olympics.entity.CompetitionType.BIATHLON) {

                allMedals.addAll(
                        getBiathlonMedals(competition.getId())
                );
            }
        });

        return statisticsService.calculateCountryMedals(allMedals);
    }

    @GetMapping("/statistics")
    public OlympicStatisticsResponse getStatistics() {

        List<MedalResponse> allMedals = new java.util.ArrayList<>();

        competitionRepository.findAll().forEach(competition -> {

            if (competition.getType() ==
                    com.example.winter_olympics.entity.CompetitionType.SKI_SLALOM) {

                allMedals.addAll(
                        getSlalomMedals(competition.getId())
                );

            } else if (competition.getType() ==
                    com.example.winter_olympics.entity.CompetitionType.BIATHLON) {

                allMedals.addAll(
                        getBiathlonMedals(competition.getId())
                );
            }
        });

        return statisticsService.getStatistics(allMedals);
    }
}