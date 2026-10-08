package com.example.winter_olympics.dto;

import java.math.BigDecimal;

public class BiathlonResultResponse {

    private Long registrationId;
    private String athleteName;
    private String country;

    private BigDecimal skiTime;
    private int misses;
    private BigDecimal penaltyPerMiss;
    private BigDecimal penaltyTime;
    private BigDecimal finalTime;

    private boolean finished;

    public BiathlonResultResponse(
            Long registrationId,
            String athleteName,
            String country,
            BigDecimal skiTime,
            int misses,
            BigDecimal penaltyPerMiss,
            BigDecimal penaltyTime,
            BigDecimal finalTime,
            boolean finished
    ) {
        this.registrationId = registrationId;
        this.athleteName = athleteName;
        this.country = country;
        this.skiTime = skiTime;
        this.misses = misses;
        this.penaltyPerMiss = penaltyPerMiss;
        this.penaltyTime = penaltyTime;
        this.finalTime = finalTime;
        this.finished = finished;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public String getAthleteName() {
        return athleteName;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getSkiTime() {
        return skiTime;
    }

    public int getMisses() {
        return misses;
    }

    public BigDecimal getPenaltyPerMiss() {
        return penaltyPerMiss;
    }

    public BigDecimal getPenaltyTime() {
        return penaltyTime;
    }

    public BigDecimal getFinalTime() {
        return finalTime;
    }

    public boolean isFinished() {
        return finished;
    }
}