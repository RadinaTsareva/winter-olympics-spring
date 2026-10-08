package com.example.winter_olympics.repository;

import com.example.winter_olympics.entity.SlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlalomResultRepository extends JpaRepository<SlalomResult, Long> {
}