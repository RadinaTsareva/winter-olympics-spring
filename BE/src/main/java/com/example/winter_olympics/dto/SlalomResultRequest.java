package com.example.winter_olympics.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SlalomResultRequest {

    @NotNull(message = "Registration is required")
    private Long registrationId;

    @NotNull(message = "Time is required")
    @DecimalMin(value = "0.001", message = "Time must be greater than 0")
    private BigDecimal time;

    @NotNull(message = "Finished status is required")
    private Boolean finished;

    public SlalomResultRequest() {
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public BigDecimal getTime() {
        return time;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}