package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}