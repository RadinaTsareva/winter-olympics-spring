package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}