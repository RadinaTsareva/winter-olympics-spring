package com.example.winter_olympics.dto;

public class CountryMedalResponse {

    private String country;
    private int gold;
    private int silver;
    private int bronze;
    private int total;

    public CountryMedalResponse(
            String country,
            int gold,
            int silver,
            int bronze
    ) {
        this.country = country;
        this.gold = gold;
        this.silver = silver;
        this.bronze = bronze;
        this.total = gold + silver + bronze;
    }

    public String getCountry() {
        return country;
    }

    public int getGold() {
        return gold;
    }

    public int getSilver() {
        return silver;
    }

    public int getBronze() {
        return bronze;
    }

    public int getTotal() {
        return total;
    }
}