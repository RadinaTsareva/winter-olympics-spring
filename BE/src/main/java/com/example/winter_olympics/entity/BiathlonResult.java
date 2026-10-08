package com.example.winter_olympics.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "biathlon_results")
public class BiathlonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private CompetitionRegistration registration;

    @Column(name = "ski_time", precision = 10, scale = 3)
    private BigDecimal skiTime;

    @Column(name = "misses", nullable = false)
    private int misses = 0;

    @Column(
            name = "penalty_per_miss",
            precision = 10,
            scale = 3,
            nullable = false
    )
    private BigDecimal penaltyPerMiss;

    @Column(name = "finished", nullable = false)
    private boolean finished = false;

    public BiathlonResult() {
    }

    public BiathlonResult(
            CompetitionRegistration registration,
            BigDecimal penaltyPerMiss
    ) {
        this.registration = registration;
        this.penaltyPerMiss = penaltyPerMiss;
    }

    public Long getId() {
        return id;
    }

    public CompetitionRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(CompetitionRegistration registration) {
        this.registration = registration;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}