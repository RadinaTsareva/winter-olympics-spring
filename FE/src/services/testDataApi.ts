import { apiRequest } from './apiClient';

export interface TestDataSummary {
  message: string;
  countriesCreated: number;
  countriesReused: number;
  athletesCreated: number;
  athletesReused: number;
  usersCreated: number;
  usersReused: number;
  demoUsernames: string[];
  competitionsCreated: number;
  competitionsReused: number;
  registrationsCreated: number;
  registrationsReused: number;
  slalomResultsCreated: number;
  slalomResultsReused: number;
  biathlonResultsCreated: number;
  biathlonResultsReused: number;
}

export interface AdminDataResetResponse {
  message: string;
  preservedAdminUsername: string;
}

export function createTestData(): Promise<TestDataSummary> {
  return apiRequest<TestDataSummary>('/api/admin/test-data', { method: 'POST' });
}

export function resetAllData(confirmation: string): Promise<AdminDataResetResponse> {
  return apiRequest<AdminDataResetResponse>('/api/admin/reset-data', {
    method: 'POST',
    body: JSON.stringify({ confirmation }),
  });
}
