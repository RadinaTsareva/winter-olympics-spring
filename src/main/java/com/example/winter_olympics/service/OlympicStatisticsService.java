package com.example.winter_olympics.service;

import com.example.winter_olympics.dto.CountryMedalResponse;
import com.example.winter_olympics.dto.MedalResponse;
import com.example.winter_olympics.dto.OlympicStatisticsResponse;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Medal;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OlympicStatisticsService {

    private final AthleteRepository athleteRepository;
    private final CompetitionRegistrationRepository registrationRepository;

    public OlympicStatisticsService(
            AthleteRepository athleteRepository,
            CompetitionRegistrationRepository registrationRepository
    ) {
        this.athleteRepository = athleteRepository;
        this.registrationRepository = registrationRepository;
    }

    public Medal getMedalForPosition(int position) {
        return switch (position) {
            case 1 -> Medal.GOLD;
            case 2 -> Medal.SILVER;
            case 3 -> Medal.BRONZE;
            default -> null;
        };
    }

    public List<CountryMedalResponse> calculateCountryMedals(
            List<MedalResponse> medals
    ) {
        Map<String, List<MedalResponse>> medalsByCountry =
                medals.stream()
                        .collect(Collectors.groupingBy(
                                MedalResponse::getCountry
                        ));

        return medalsByCountry.entrySet()
                .stream()
                .map(entry -> {

                    int gold = (int) entry.getValue().stream()
                            .filter(medal -> medal.getMedal() == Medal.GOLD)
                            .count();

                    int silver = (int) entry.getValue().stream()
                            .filter(medal -> medal.getMedal() == Medal.SILVER)
                            .count();

                    int bronze = (int) entry.getValue().stream()
                            .filter(medal -> medal.getMedal() == Medal.BRONZE)
                            .count();

                    return new CountryMedalResponse(
                            entry.getKey(),
                            gold,
                            silver,
                            bronze
                    );
                })
                .sorted(
                        Comparator.comparingInt(
                                        CountryMedalResponse::getGold
                                )
                                .reversed()
                                .thenComparing(
                                        CountryMedalResponse::getSilver,
                                        Comparator.reverseOrder()
                                )
                                .thenComparing(
                                        CountryMedalResponse::getBronze,
                                        Comparator.reverseOrder()
                                )
                )
                .toList();
    }

    public OlympicStatisticsResponse getStatistics(
            List<MedalResponse> allMedals
    ) {

        List<Athlete> participants = registrationRepository.findAll()
                .stream()
                .map(registration -> registration.getAthlete())
                .distinct()
                .toList();

        double averageAge = participants.stream()
                .mapToInt(this::calculateAge)
                .average()
                .orElse(0.0);

        List<String> medalistNames = allMedals.stream()
                .map(MedalResponse::getAthleteName)
                .toList();

        List<Athlete> medalists = athleteRepository.findAll()
                .stream()
                .filter(athlete ->
                        medalistNames.contains(athlete.getName())
                )
                .toList();

        OlympicStatisticsResponse.AthleteAgeResponse youngest =
                medalists.stream()
                        .min(Comparator.comparingInt(this::calculateAge))
                        .map(athlete ->
                                new OlympicStatisticsResponse.AthleteAgeResponse(
                                        athlete.getName(),
                                        calculateAge(athlete)
                                )
                        )
                        .orElse(null);

        OlympicStatisticsResponse.AthleteAgeResponse oldest =
                medalists.stream()
                        .max(Comparator.comparingInt(this::calculateAge))
                        .map(athlete ->
                                new OlympicStatisticsResponse.AthleteAgeResponse(
                                        athlete.getName(),
                                        calculateAge(athlete)
                                )
                        )
                        .orElse(null);

        return new OlympicStatisticsResponse(
                Math.round(averageAge * 100.0) / 100.0,
                youngest,
                oldest
        );
    }

    private int calculateAge(Athlete athlete) {
        return Period.between(
                athlete.getDateOfBirth(),
                LocalDate.now()
        ).getYears();
    }
}