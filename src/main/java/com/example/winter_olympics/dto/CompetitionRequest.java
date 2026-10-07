package com.example.winter_olympics.dto;

import com.example.winter_olympics.entity.CompetitionType;
import com.example.winter_olympics.entity.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompetitionRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Competition type is required")
    private CompetitionType type;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Min(value = 1, message = "Minimum age must be at least 1")
    private int minimumAge;

    @Min(value = 1, message = "Number of laps must be at least 1")
    private Integer numberOfLaps;

    @Min(value = 1, message = "Shooting lap must be at least 1")
    private Integer shootingAfterLaps;

    public CompetitionRequest() {
    }

    public String getName() {
        return name;
    }

    public CompetitionType getType() {
        return type;
    }

    public Gender getGender() {
        return gender;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getNumberOfLaps() {
        return numberOfLaps;
    }

    public void setNumberOfLaps(Integer numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    public Integer getShootingAfterLaps() {
        return shootingAfterLaps;
    }

    public void setShootingAfterLaps(Integer shootingAfterLaps) {
        this.shootingAfterLaps = shootingAfterLaps;
    }
}