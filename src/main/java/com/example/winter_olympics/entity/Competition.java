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

    public Competition() {
    }

    public Competition(
            String name,
            CompetitionType type,
            Gender gender,
            int minimumAge
    ) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.minimumAge = minimumAge;
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
}