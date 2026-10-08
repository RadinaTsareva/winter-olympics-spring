package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.SlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlalomResultRepository extends JpaRepository<SlalomResult, Long> {
    Optional<SlalomResult> findFirstByRegistration_Id(Long registrationId);
}
