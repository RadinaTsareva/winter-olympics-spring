import type { AthleteProfileData, AthleteProfileUpdate, CountryOption } from '../types';
import { apiRequest } from './apiClient';

export function getMyAthleteProfile(): Promise<AthleteProfileData> {
  return apiRequest<AthleteProfileData>('/api/athletes/me');
}

export function getAthletes(): Promise<AthleteProfileData[]> {
  return apiRequest<AthleteProfileData[]>('/api/athletes');
}

export function getCountries(): Promise<CountryOption[]> {
  return apiRequest<CountryOption[]>('/api/countries');
}

export function createAthlete(profile: AthleteProfileUpdate): Promise<AthleteProfileData> {
  return apiRequest<AthleteProfileData>('/api/athletes', {
    method: 'POST',
    body: JSON.stringify(profile),
  });
}

export function updateAthleteProfile(id: number, profile: AthleteProfileUpdate): Promise<void> {
  return apiRequest<void>(`/api/athletes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(profile),
  });
}

export function deleteAthleteProfile(id: number): Promise<void> {
  return apiRequest<void>(`/api/athletes/${id}`, { method: 'DELETE' });
}
