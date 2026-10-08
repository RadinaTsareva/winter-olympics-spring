// Common types used across the application

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'ADMIN' | 'ATHLETE' | 'USER';
}

export interface Competition {
  id: number;
  name: string;
  type: 'SKI_SLALOM' | 'BIATHLON';
  gender: 'MALE' | 'FEMALE';
  minimumAge: number;
  numberOfLaps: number | null;
  shootingAfterLaps: number | null;
}

export interface SlalomRankingEntry {
  position: number;
  athleteName: string;
  country: string;
  firstRunTime: number | null;
  secondRunTime: number | null;
  finalTime: number | null;
}

export interface BiathlonRankingEntry {
  position: number;
  athleteName: string;
  country: string;
  skiTime: number | null;
  misses: number;
  penaltyTime: number | null;
  finalTime: number | null;
}

export interface CountryMedalStanding {
  country: string;
  gold: number;
  silver: number;
  bronze: number;
  total: number;
}

export interface CompetitionMedal {
  position: number;
  athleteName: string;
  country: string;
  medal: 'GOLD' | 'SILVER' | 'BRONZE';
}

export interface AthleteAgeStatistic {
  athleteName: string;
  age: number;
}

export interface OlympicStatistics {
  averageParticipantAge: number | null;
  youngestMedalist: AthleteAgeStatistic | null;
  oldestMedalist: AthleteAgeStatistic | null;
}

export interface Athlete {
  id: number;
  firstName: string;
  lastName: string;
  country: string;
  birthDate: string;
  sport: 'SLALOM' | 'BIATHLON';
}

export interface AthleteProfileData {
  id: number;
  name: string;
  countryId: number;
  country: string;
  gender: 'MALE' | 'FEMALE';
  dateOfBirth: string;
}

export interface CountryOption {
  id: number;
  name: string;
}

export interface AthleteProfileUpdate {
  name: string;
  countryId: number;
  gender: 'MALE' | 'FEMALE';
  dateOfBirth: string;
}

export interface CompetitionRegistration {
  id: number;
  athleteId: number;
  athleteName: string;
  competitionId: number;
  competitionName: string;
}

export interface SlalomResult {
  id: number;
  athleteId: number;
  competitionId: number;
  run1Time: number;
  run2Time: number;
  totalTime: number;
  rank: number;
}

export interface BiathlonResult {
  id: number;
  athleteId: number;
  competitionId: number;
  skatingTime: number;
  shootingAccuracy: number;
  totalTime: number;
  rank: number;
}

export interface Country {
  id: number;
  name: string;
  code: string;
  medalCount?: number;
}
