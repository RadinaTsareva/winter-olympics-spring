package com.example.winter_olympics.dto;

import java.math.BigDecimal;

public class BiathlonRankingResponse {

    private int position;
    private String athleteName;
    private String country;
    private BigDecimal skiTime;
    private int misses;
    private BigDecimal penaltyTime;
    private BigDecimal finalTime;

    public BiathlonRankingResponse(
            int position,
            String athleteName,
            String country,
            BigDecimal skiTime,
            int misses,
            BigDecimal penaltyTime,
            BigDecimal finalTime
    ) {
        this.position = position;
        this.athleteName = athleteName;
        this.country = country;
        this.skiTime = skiTime;
        this.misses = misses;
        this.penaltyTime = penaltyTime;
        this.finalTime = finalTime;
    }

    public int getPosition() {
        return position;
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

    public BigDecimal getPenaltyTime() {
        return penaltyTime;
    }

    public BigDecimal getFinalTime() {
        return finalTime;
    }
}