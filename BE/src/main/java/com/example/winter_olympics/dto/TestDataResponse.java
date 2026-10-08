package com.example.winter_olympics.dto;

import java.util.List;

public class TestDataResponse {

    private final String message;
    private final int countriesCreated;
    private final int countriesReused;
    private final int athletesCreated;
    private final int athletesReused;
    private final int usersCreated;
    private final int usersReused;
    private final List<String> demoUsernames;
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
        this(message, countriesCreated, countriesReused, athletesCreated, athletesReused,
                0, 0, List.of(), competitionsCreated, competitionsReused,
                registrationsCreated, registrationsReused, slalomResultsCreated,
                slalomResultsReused, biathlonResultsCreated, biathlonResultsReused);
    }

    public TestDataResponse(
            String message,
            int countriesCreated, int countriesReused,
            int athletesCreated, int athletesReused,
            int usersCreated, int usersReused, List<String> demoUsernames,
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
        this.usersCreated = usersCreated;
        this.usersReused = usersReused;
        this.demoUsernames = List.copyOf(demoUsernames);
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
    public int getUsersCreated() { return usersCreated; }
    public int getUsersReused() { return usersReused; }
    public List<String> getDemoUsernames() { return demoUsernames; }
    public int getCompetitionsCreated() { return competitionsCreated; }
    public int getCompetitionsReused() { return competitionsReused; }
    public int getRegistrationsCreated() { return registrationsCreated; }
    public int getRegistrationsReused() { return registrationsReused; }
    public int getSlalomResultsCreated() { return slalomResultsCreated; }
    public int getSlalomResultsReused() { return slalomResultsReused; }
    public int getBiathlonResultsCreated() { return biathlonResultsCreated; }
    public int getBiathlonResultsReused() { return biathlonResultsReused; }
}
