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
  location: string;
  startDate: string;
  endDate: string;
  sport: 'SLALOM' | 'BIATHLON';
  description?: string;
}

export interface Athlete {
  id: number;
  firstName: string;
  lastName: string;
  country: string;
  birthDate: string;
  sport: 'SLALOM' | 'BIATHLON';
}

export interface CompetitionRegistration {
  id: number;
  athleteId: number;
  competitionId: number;
  registrationDate: string;
  status: 'REGISTERED' | 'WITHDRAWN' | 'COMPLETED';
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

