export const AUTH_STORAGE_KEY = 'winter-olympics-auth';

export function getStoredAuthToken(): string | null {
  try {
    const stored = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!stored) return null;
    const session = JSON.parse(stored) as { token?: unknown };
    return typeof session.token === 'string' ? session.token : null;
  } catch {
    return null;
  }
}
