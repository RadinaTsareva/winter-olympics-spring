package com.example.winter_olympics.dto;

import com.example.winter_olympics.entity.Medal;

public class MedalResponse {

    private int position;
    private String athleteName;
    private String country;
    private Medal medal;

    public MedalResponse(
            int position,
            String athleteName,
            String country,
            Medal medal
    ) {
        this.position = position;
        this.athleteName = athleteName;
        this.country = country;
        this.medal = medal;
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

    public Medal getMedal() {
        return medal;
    }
}