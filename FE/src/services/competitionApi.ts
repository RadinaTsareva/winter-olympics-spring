import type {
  BiathlonRankingEntry,
  Competition,
  CompetitionMedal,
  CountryMedalStanding,
  OlympicStatistics,
  SlalomRankingEntry,
} from '../types';
import { ApiError, apiRequest } from './apiClient';

export class CompetitionNotFoundError extends Error {
  constructor() {
    super('Competition not found.');
    this.name = 'CompetitionNotFoundError';
  }
}

export async function getCompetitions(): Promise<Competition[]> {
  return apiRequest<Competition[]>('/api/competitions');
}

export async function getCompetition(id: number): Promise<Competition> {
  try {
    return await apiRequest<Competition>(`/api/competitions/${id}`);
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      throw new CompetitionNotFoundError();
    }
    throw error;
  }
}

async function getRanking<T>(path: string): Promise<T[]> {
  return apiRequest<T[]>(path);
}

export function getSlalomRanking(competitionId: number): Promise<SlalomRankingEntry[]> {
  return getRanking<SlalomRankingEntry>(`/api/slalom-results/ranking/${competitionId}`);
}

export function getBiathlonRanking(competitionId: number): Promise<BiathlonRankingEntry[]> {
  return getRanking<BiathlonRankingEntry>(`/api/biathlon-results/ranking/${competitionId}`);
}

async function getMedals<T>(path: string): Promise<T[]> {
  return apiRequest<T[]>(path);
}

export function getCountryMedals(): Promise<CountryMedalStanding[]> {
  return getMedals<CountryMedalStanding>('/api/olympics/medals');
}

export function getSlalomMedals(competitionId: number): Promise<CompetitionMedal[]> {
  return getMedals<CompetitionMedal>(`/api/olympics/medals/slalom/${competitionId}`);
}

export function getBiathlonMedals(competitionId: number): Promise<CompetitionMedal[]> {
  return getMedals<CompetitionMedal>(`/api/olympics/medals/biathlon/${competitionId}`);
}

export async function getOlympicStatistics(): Promise<OlympicStatistics | null> {
  return apiRequest<OlympicStatistics | null>('/api/olympics/statistics');
}
