package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.BiathlonResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiathlonResultRepository
        extends JpaRepository<BiathlonResult, Long> {
}