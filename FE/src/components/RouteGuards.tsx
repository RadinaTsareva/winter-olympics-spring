import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@context/AuthContext';
import type { AuthRole } from '@services/authApi';

export function RequireRole({ role, children }: { role: AuthRole; children: ReactNode }) {
  const { session } = useAuth();

  if (!session) return <Navigate to="/login" replace />;
  if (session.role !== role) {
    return <Navigate to={session.role === 'ADMIN' ? '/admin' : '/athlete'} replace />;
  }
  return <>{children}</>;
}

export function RedirectAuthenticated({ children }: { children: ReactNode }) {
  const { session } = useAuth();
  if (session) return <Navigate to={session.role === 'ADMIN' ? '/admin' : '/athlete'} replace />;
  return <>{children}</>;
}
