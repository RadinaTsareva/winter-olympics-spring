package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    Optional<Athlete> findFirstByNameAndCountry_IdAndGenderAndDateOfBirth(
            String name, Long countryId, Gender gender, LocalDate dateOfBirth
    );
}
