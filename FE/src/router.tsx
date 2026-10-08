import { createBrowserRouter, RouteObject } from 'react-router-dom';
import { AppLayout } from '@layouts/AppLayout';

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
      <AppLayout>
        <Login />
      </AppLayout>
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
    path: '/athlete',
    element: (
      <AppLayout>
        <AthleteDashboard />
      </AppLayout>
    ),
  },
  {
    path: '/athlete/profile',
    element: (
      <AppLayout>
        <AthleteProfile />
      </AppLayout>
    ),
  },
  {
    path: '/athlete/competitions',
    element: (
      <AppLayout>
        <AthleteCompetitions />
      </AppLayout>
    ),
  },
];

const adminRoutes: RouteObject[] = [
  {
    path: '/admin',
    element: (
      <AppLayout>
        <AdminDashboard />
      </AppLayout>
    ),
  },
  {
    path: '/admin/athletes',
    element: (
      <AppLayout>
        <AdminAthletes />
      </AppLayout>
    ),
  },
  {
    path: '/admin/competitions',
    element: (
      <AppLayout>
        <AdminCompetitions />
      </AppLayout>
    ),
  },
  {
    path: '/admin/registrations',
    element: (
      <AppLayout>
        <AdminRegistrations />
      </AppLayout>
    ),
  },
  {
    path: '/admin/slalom-results',
    element: (
      <AppLayout>
        <AdminSlalomResults />
      </AppLayout>
    ),
  },
  {
    path: '/admin/biathlon-results',
    element: (
      <AppLayout>
        <AdminBiathlonResults />
      </AppLayout>
    ),
  },
];

const routes: RouteObject[] = [...publicRoutes, ...athleteRoutes, ...adminRoutes];

export const router = createBrowserRouter(routes);

