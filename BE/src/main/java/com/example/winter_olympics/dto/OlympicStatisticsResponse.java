package com.example.winter_olympics.dto;

public class OlympicStatisticsResponse {

    private double averageParticipantAge;
    private AthleteAgeResponse youngestMedalist;
    private AthleteAgeResponse oldestMedalist;

    public OlympicStatisticsResponse(
            double averageParticipantAge,
            AthleteAgeResponse youngestMedalist,
            AthleteAgeResponse oldestMedalist
    ) {
        this.averageParticipantAge = averageParticipantAge;
        this.youngestMedalist = youngestMedalist;
        this.oldestMedalist = oldestMedalist;
    }

    public double getAverageParticipantAge() {
        return averageParticipantAge;
    }

    public AthleteAgeResponse getYoungestMedalist() {
        return youngestMedalist;
    }

    public AthleteAgeResponse getOldestMedalist() {
        return oldestMedalist;
    }

    public static class AthleteAgeResponse {

        private String athleteName;
        private int age;

        public AthleteAgeResponse(String athleteName, int age) {
            this.athleteName = athleteName;
            this.age = age;
        }

        public String getAthleteName() {
            return athleteName;
        }

        public int getAge() {
            return age;
        }
    }
}