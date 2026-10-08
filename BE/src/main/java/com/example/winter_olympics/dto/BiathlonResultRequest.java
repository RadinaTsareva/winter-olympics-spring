package com.example.winter_olympics.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BiathlonResultRequest {

    @NotNull(message = "Registration is required")
    private Long registrationId;

    @NotNull(message = "Ski time is required")
    @DecimalMin(value = "0.001", message = "Ski time must be greater than 0")
    private BigDecimal skiTime;

    @Min(value = 0, message = "Misses cannot be negative")
    private int misses;

    @NotNull(message = "Penalty per miss is required")
    @DecimalMin(value = "0.001", message = "Penalty must be greater than 0")
    private BigDecimal penaltyPerMiss;

    @NotNull(message = "Finished status is required")
    private Boolean finished;

    public BiathlonResultRequest() {
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public BigDecimal getSkiTime() {
        return skiTime;
    }

    public void setSkiTime(BigDecimal skiTime) {
        this.skiTime = skiTime;
    }

    public int getMisses() {
        return misses;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public BigDecimal getPenaltyPerMiss() {
        return penaltyPerMiss;
    }

    public void setPenaltyPerMiss(BigDecimal penaltyPerMiss) {
        this.penaltyPerMiss = penaltyPerMiss;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}