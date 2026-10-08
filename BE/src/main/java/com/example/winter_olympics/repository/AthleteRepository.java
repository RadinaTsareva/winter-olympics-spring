package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
}