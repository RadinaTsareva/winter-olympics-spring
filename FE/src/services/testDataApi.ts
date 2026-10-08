import { apiRequest } from './apiClient';

export interface TestDataSummary {
  message: string;
  countriesCreated: number;
  countriesReused: number;
  athletesCreated: number;
  athletesReused: number;
  competitionsCreated: number;
  competitionsReused: number;
  registrationsCreated: number;
  registrationsReused: number;
  slalomResultsCreated: number;
  slalomResultsReused: number;
  biathlonResultsCreated: number;
  biathlonResultsReused: number;
}

export function createTestData(): Promise<TestDataSummary> {
  return apiRequest<TestDataSummary>('/api/admin/test-data', { method: 'POST' });
}
