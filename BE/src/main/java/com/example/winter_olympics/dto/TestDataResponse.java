package com.example.winter_olympics.dto;

public class TestDataResponse {

    private final String message;
    private final int countriesCreated;
    private final int countriesReused;
    private final int athletesCreated;
    private final int athletesReused;
    private final int competitionsCreated;
    private final int competitionsReused;
    private final int registrationsCreated;
    private final int registrationsReused;
    private final int slalomResultsCreated;
    private final int slalomResultsReused;
    private final int biathlonResultsCreated;
    private final int biathlonResultsReused;

    public TestDataResponse(
            String message,
            int countriesCreated, int countriesReused,
            int athletesCreated, int athletesReused,
            int competitionsCreated, int competitionsReused,
            int registrationsCreated, int registrationsReused,
            int slalomResultsCreated, int slalomResultsReused,
            int biathlonResultsCreated, int biathlonResultsReused
    ) {
        this.message = message;
        this.countriesCreated = countriesCreated;
        this.countriesReused = countriesReused;
        this.athletesCreated = athletesCreated;
        this.athletesReused = athletesReused;
        this.competitionsCreated = competitionsCreated;
        this.competitionsReused = competitionsReused;
        this.registrationsCreated = registrationsCreated;
        this.registrationsReused = registrationsReused;
        this.slalomResultsCreated = slalomResultsCreated;
        this.slalomResultsReused = slalomResultsReused;
        this.biathlonResultsCreated = biathlonResultsCreated;
        this.biathlonResultsReused = biathlonResultsReused;
    }

    public String getMessage() { return message; }
    public int getCountriesCreated() { return countriesCreated; }
    public int getCountriesReused() { return countriesReused; }
    public int getAthletesCreated() { return athletesCreated; }
    public int getAthletesReused() { return athletesReused; }
    public int getCompetitionsCreated() { return competitionsCreated; }
    public int getCompetitionsReused() { return competitionsReused; }
    public int getRegistrationsCreated() { return registrationsCreated; }
    public int getRegistrationsReused() { return registrationsReused; }
    public int getSlalomResultsCreated() { return slalomResultsCreated; }
    public int getSlalomResultsReused() { return slalomResultsReused; }
    public int getBiathlonResultsCreated() { return biathlonResultsCreated; }
    public int getBiathlonResultsReused() { return biathlonResultsReused; }
}
