package com.example.winter_olympics.dto;

import com.example.winter_olympics.entity.Gender;

import java.time.LocalDate;

public class AthleteResponse {

    private Long id;
    private String name;
    private Long countryId;
    private String country;
    private Gender gender;
    private LocalDate dateOfBirth;

    public AthleteResponse(
            Long id,
            String name,
            Long countryId,
            String country,
            Gender gender,
            LocalDate dateOfBirth
    ) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.country = country;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCountryId() {
        return countryId;
    }

    public String getCountry() {
        return country;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}