import { createBrowserRouter, Navigate, RouteObject } from 'react-router-dom';
import { AppLayout } from '@layouts/AppLayout';
import { RedirectAuthenticated, RequireRole } from '@components/RouteGuards';

// Public pages
import { Home } from '@pages/Home';
import { Competitions } from '@pages/Competitions';
import { CompetitionDetails } from '@pages/CompetitionDetails';
import { Rankings } from '@pages/Rankings';
import { Medals } from '@pages/Medals';
import { Statistics } from '@pages/Statistics';
import { Login } from '@pages/Login';
import { Register } from '@pages/Register';

// Athlete pages
import { AthleteDashboard } from '@pages/AthleteDashboard';
import { AthleteProfile } from '@pages/AthleteProfile';
import { AthleteCompetitions } from '@pages/AthleteCompetitions';

// Admin pages
import { AdminDashboard } from '@pages/AdminDashboard';
import { AdminAthletes } from '@pages/AdminAthletes';
import { AdminCompetitions } from '@pages/AdminCompetitions';
import { AdminRegistrations } from '@pages/AdminRegistrations';
import { AdminSlalomResults } from '@pages/AdminSlalomResults';
import { AdminBiathlonResults } from '@pages/AdminBiathlonResults';

const publicRoutes: RouteObject[] = [
  {
    path: '/',
    element: (
      <AppLayout>
        <Home />
      </AppLayout>
    ),
  },
  {
    path: '/competitions',
    element: (
      <AppLayout>
        <Competitions />
      </AppLayout>
    ),
  },
  {
    path: '/competitions/:id',
    element: (
      <AppLayout>
        <CompetitionDetails />
      </AppLayout>
    ),
  },
  {
    path: '/rankings',
    element: (
      <AppLayout>
        <Rankings />
      </AppLayout>
    ),
  },
  {
    path: '/medals',
    element: (
      <AppLayout>
        <Medals />
      </AppLayout>
    ),
  },
  {
    path: '/statistics',
    element: (
      <AppLayout>
        <Statistics />
      </AppLayout>
    ),
  },
  {
    path: '/login',
    element: (
      <RedirectAuthenticated>
        <AppLayout><Login /></AppLayout>
      </RedirectAuthenticated>
    ),
  },
  {
    path: '/register',
    element: (
      <AppLayout>
        <Register />
      </AppLayout>
    ),
  },
];

const athleteRoutes: RouteObject[] = [
  {
    path: '/dashboard',
    element: <RequireRole role="ATHLETE"><Navigate to="/athlete" replace /></RequireRole>,
  },
  {
    path: '/athlete',
    element: (
      <RequireRole role="ATHLETE"><AppLayout><AthleteDashboard /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/athlete/profile',
    element: (
      <RequireRole role="ATHLETE"><AppLayout><AthleteProfile /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/athlete/competitions',
    element: (
      <RequireRole role="ATHLETE"><AppLayout><AthleteCompetitions /></AppLayout></RequireRole>
    ),
  },
];

const adminRoutes: RouteObject[] = [
  {
    path: '/admin',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminDashboard /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/admin/athletes',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminAthletes /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/admin/competitions',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminCompetitions /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/admin/registrations',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminRegistrations /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/admin/slalom-results',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminSlalomResults /></AppLayout></RequireRole>
    ),
  },
  {
    path: '/admin/biathlon-results',
    element: (
      <RequireRole role="ADMIN"><AppLayout><AdminBiathlonResults /></AppLayout></RequireRole>
    ),
  },
];

const protectedFallbackRoutes: RouteObject[] = [
  {
    path: '/athlete/*',
    element: <RequireRole role="ATHLETE"><Navigate to="/athlete" replace /></RequireRole>,
  },
  {
    path: '/admin/*',
    element: <RequireRole role="ADMIN"><Navigate to="/admin" replace /></RequireRole>,
  },
];

const routes: RouteObject[] = [...publicRoutes, ...athleteRoutes, ...adminRoutes, ...protectedFallbackRoutes];

export const router = createBrowserRouter(routes);
