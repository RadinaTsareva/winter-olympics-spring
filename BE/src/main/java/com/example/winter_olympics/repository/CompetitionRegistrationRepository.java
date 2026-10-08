package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.CompetitionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRegistrationRepository
        extends JpaRepository<CompetitionRegistration, Long> {
    java.util.Optional<CompetitionRegistration> findFirstByAthlete_IdAndCompetition_Id(
            Long athleteId, Long competitionId
    );

    boolean existsByAthleteIdAndCompetitionId(
            Long athleteId,
            Long competitionId
    );
}
