package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.entity.CompetitionType;
import com.example.winter_olympics.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    Optional<Competition> findFirstByNameAndTypeAndGender(
            String name, CompetitionType type, Gender gender
    );
}
