package com.example.winter_olympics.dto;

public class CompetitionRegistrationResponse {

    private Long id;
    private Long athleteId;
    private String athleteName;
    private Long competitionId;
    private String competitionName;

    public CompetitionRegistrationResponse(
            Long id,
            Long athleteId,
            String athleteName,
            Long competitionId,
            String competitionName
    ) {
        this.id = id;
        this.athleteId = athleteId;
        this.athleteName = athleteName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public Long getId() {
        return id;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public String getAthleteName() {
        return athleteName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}