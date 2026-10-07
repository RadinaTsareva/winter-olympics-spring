package com.example.winter_olympics.dto;

import jakarta.validation.constraints.NotNull;

public class RegistrationRequest {

    @NotNull(message = "Athlete is required")
    private Long athleteId;

    @NotNull(message = "Competition is required")
    private Long competitionId;

    public RegistrationRequest() {
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}