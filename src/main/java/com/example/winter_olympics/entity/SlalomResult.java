package com.example.winter_olympics.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "slalom_results")
public class SlalomResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private CompetitionRegistration registration;

    @Column(name = "first_run_time", precision = 10, scale = 3)
    private BigDecimal firstRunTime;

    @Column(name = "second_run_time", precision = 10, scale = 3)
    private BigDecimal secondRunTime;

    @Column(name = "first_run_finished", nullable = false)
    private boolean firstRunFinished = false;

    @Column(name = "second_run_finished", nullable = false)
    private boolean secondRunFinished = false;

    public SlalomResult() {
    }

    public SlalomResult(CompetitionRegistration registration) {
        this.registration = registration;
    }

    public Long getId() {
        return id;
    }

    public CompetitionRegistration getRegistration() {
        return registration;
    }

    public BigDecimal getFirstRunTime() {
        return firstRunTime;
    }

    public BigDecimal getSecondRunTime() {
        return secondRunTime;
    }

    public boolean isFirstRunFinished() {
        return firstRunFinished;
    }

    public boolean isSecondRunFinished() {
        return secondRunFinished;
    }

    public void setRegistration(CompetitionRegistration registration) {
        this.registration = registration;
    }

    public void setFirstRunTime(BigDecimal firstRunTime) {
        this.firstRunTime = firstRunTime;
    }

    public void setSecondRunTime(BigDecimal secondRunTime) {
        this.secondRunTime = secondRunTime;
    }

    public void setFirstRunFinished(boolean firstRunFinished) {
        this.firstRunFinished = firstRunFinished;
    }

    public void setSecondRunFinished(boolean secondRunFinished) {
        this.secondRunFinished = secondRunFinished;
    }
}