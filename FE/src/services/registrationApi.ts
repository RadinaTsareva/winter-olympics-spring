import type { CompetitionRegistration } from '../types';
import { apiRequest } from './apiClient';

export function getRegistrations(): Promise<CompetitionRegistration[]> {
  return apiRequest<CompetitionRegistration[]>('/api/registrations');
}

export function registerForCompetition(athleteId: number, competitionId: number): Promise<CompetitionRegistration> {
  return apiRequest<CompetitionRegistration>('/api/registrations', {
    method: 'POST',
    body: JSON.stringify({ athleteId, competitionId }),
  });
}

export function unregisterFromCompetition(registrationId: number): Promise<void> {
  return apiRequest<void>(`/api/registrations/${registrationId}`, { method: 'DELETE' });
}
