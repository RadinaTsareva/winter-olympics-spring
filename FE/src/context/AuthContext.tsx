import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { login as loginRequest, type AuthCredentials, type AuthSession } from '@services/authApi';
import { AUTH_STORAGE_KEY } from '@services/authStorage';

interface AuthContextValue {
  session: AuthSession | null;
  signIn: (credentials: AuthCredentials) => Promise<AuthSession>;
  signOut: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readInitialSession(): AuthSession | null {
  try {
    const stored = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!stored) return null;
    const session = JSON.parse(stored) as Partial<AuthSession>;
    if (
      typeof session.token === 'string' &&
      typeof session.username === 'string' &&
      (session.role === 'ATHLETE' || session.role === 'ADMIN')
    ) {
      return session as AuthSession;
    }
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY);
  }
  return null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(readInitialSession);

  const signIn = async (credentials: AuthCredentials) => {
    const authenticatedSession = await loginRequest(credentials);
    setSession(authenticatedSession);
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(authenticatedSession));
    return authenticatedSession;
  };

  const signOut = () => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    setSession(null);
  };

  const value = useMemo(() => ({ session, signIn, signOut }), [session]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider.');
  return context;
}
