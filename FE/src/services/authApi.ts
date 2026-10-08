import { apiRequest } from './apiClient';

export type AuthRole = 'ATHLETE' | 'ADMIN';

export interface AuthSession {
  token: string;
  username: string;
  role: AuthRole;
}

export interface AuthCredentials {
  username: string;
  password: string;
}

export async function login(credentials: AuthCredentials): Promise<AuthSession> {
  return apiRequest<AuthSession>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }, false);
}

export async function register(credentials: AuthCredentials): Promise<AuthSession> {
  return apiRequest<AuthSession>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }, false);
}
