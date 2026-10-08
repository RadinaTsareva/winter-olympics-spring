package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    java.util.Optional<Country> findByNameIgnoreCase(String name);
}
