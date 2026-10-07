package com.example.winter_olympics.dto;

import java.math.BigDecimal;

public class SlalomRankingResponse {

    private int position;
    private String athleteName;
    private String country;
    private BigDecimal firstRunTime;
    private BigDecimal secondRunTime;
    private BigDecimal finalTime;

    public SlalomRankingResponse(
            int position,
            String athleteName,
            String country,
            BigDecimal firstRunTime,
            BigDecimal secondRunTime,
            BigDecimal finalTime
    ) {
        this.position = position;
        this.athleteName = athleteName;
        this.country = country;
        this.firstRunTime = firstRunTime;
        this.secondRunTime = secondRunTime;
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

    public BigDecimal getFirstRunTime() {
        return firstRunTime;
    }

    public BigDecimal getSecondRunTime() {
        return secondRunTime;
    }

    public BigDecimal getFinalTime() {
        return finalTime;
    }
}