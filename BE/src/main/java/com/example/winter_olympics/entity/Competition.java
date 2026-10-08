package com.example.winter_olympics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "minimum_age", nullable = false)
    private int minimumAge;

    @Column(name = "number_of_laps")
    private Integer numberOfLaps;

    @Column(name = "shooting_after_laps")
    private Integer shootingAfterLaps;

    public Competition() {
    }

    public Competition(
            String name,
            CompetitionType type,
            Gender gender,
            int minimumAge,
            Integer numberOfLaps,
            Integer shootingAfterLaps
    ) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.minimumAge = minimumAge;
        this.numberOfLaps = numberOfLaps;
        this.shootingAfterLaps = shootingAfterLaps;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CompetitionType getType() {
        return type;
    }

    public Gender getGender() {
        return gender;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getNumberOfLaps() {
        return numberOfLaps;
    }

    public void setNumberOfLaps(Integer numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    public Integer getShootingAfterLaps() {
        return shootingAfterLaps;
    }

    public void setShootingAfterLaps(Integer shootingAfterLaps) {
        this.shootingAfterLaps = shootingAfterLaps;
    }
}