package com.example.winter_olympics.dto;

import java.math.BigDecimal;

public class SlalomResultResponse {

    private Long registrationId;
    private String athleteName;
    private String country;
    private BigDecimal firstRunTime;
    private boolean firstRunFinished;
    private BigDecimal secondRunTime;
    private boolean secondRunFinished;

    public SlalomResultResponse(
            Long registrationId,
            String athleteName,
            String country,
            BigDecimal firstRunTime,
            boolean firstRunFinished,
            BigDecimal secondRunTime,
            boolean secondRunFinished
    ) {
        this.registrationId = registrationId;
        this.athleteName = athleteName;
        this.country = country;
        this.firstRunTime = firstRunTime;
        this.firstRunFinished = firstRunFinished;
        this.secondRunTime = secondRunTime;
        this.secondRunFinished = secondRunFinished;
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

    public BigDecimal getFirstRunTime() {
        return firstRunTime;
    }

    public boolean isFirstRunFinished() {
        return firstRunFinished;
    }

    public BigDecimal getSecondRunTime() {
        return secondRunTime;
    }

    public boolean isSecondRunFinished() {
        return secondRunFinished;
    }
}