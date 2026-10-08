import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { ApiError } from '@services/apiClient';
import { getMyAthleteProfile } from '@services/athleteApi';
import { getCompetitions } from '@services/competitionApi';
import { getRegistrations } from '@services/registrationApi';
import type { AthleteProfileData, Competition, CompetitionRegistration } from '../types';
import '@styles/content-page.css';
import '@styles/athlete-dashboard.css';

function errorMessage(error: unknown, area: string): string {
  if (error instanceof ApiError) {
    if (error.status === 401) return 'Your session has expired. Sign in again to view your athlete dashboard.';
    if (error.status === 403) return 'Your account is not authorized to view this athlete information.';
    if (error.status === 404 && area === 'profile') return 'There is no athlete profile linked to this account yet.';
    return error.message || `Could not load ${area}.`;
  }
  if (error instanceof Error && /fetch|network/i.test(error.message)) {
    return 'Could not connect to the Winter Olympics server. Check your connection and retry.';
  }
  return `Could not load ${area}. Please try again.`;
}

const disciplineLabel = (type: Competition['type'] | undefined) => {
  if (type === 'SKI_SLALOM') return 'Ski Slalom';
  if (type === 'BIATHLON') return 'Biathlon';
  return 'Competition type unavailable';
};

const genderLabel = (gender: Competition['gender'] | undefined) => {
  if (gender === 'MALE') return "Men's event";
  if (gender === 'FEMALE') return "Women's event";
  return 'Gender unavailable';
};

function formatDate(date: string): string {
  const parsed = new Date(`${date}T00:00:00`);
  return Number.isNaN(parsed.getTime())
    ? date
    : parsed.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
}

function LoadError({ message, retry }: { message: string; retry: () => void }) {
  return (
    <div className="athlete-dashboard-error" role="alert">
      <span className="athlete-dashboard-state-icon" aria-hidden="true">!</span>
      <div><strong>We couldn&apos;t load this section</strong><p>{message}</p></div>
      <button type="button" onClick={retry}>Retry</button>
    </div>
  );
}

export const AthleteDashboard: React.FC = () => {
  const [athlete, setAthlete] = useState<AthleteProfileData | null>(null);
  const [registrations, setRegistrations] = useState<CompetitionRegistration[]>([]);
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [registrationsError, setRegistrationsError] = useState<string | null>(null);
  const [competitionsError, setCompetitionsError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let isCurrent = true;
    setLoading(true);
    setProfileError(null);
    setRegistrationsError(null);
    setCompetitionsError(null);

    const loadDashboard = async () => {
      const [profileResult, registrationsResult, competitionsResult] = await Promise.allSettled([
        getMyAthleteProfile(),
        getRegistrations(),
        getCompetitions(),
      ]);

      if (!isCurrent) return;
      if (profileResult.status === 'fulfilled') {
        setAthlete(profileResult.value);
      } else {
        setAthlete(null);
        setProfileError(errorMessage(profileResult.reason, 'profile'));
      }

      if (registrationsResult.status === 'fulfilled') {
        setRegistrations(registrationsResult.value);
      } else {
        setRegistrations([]);
        setRegistrationsError(errorMessage(registrationsResult.reason, 'your registrations'));
      }

      if (competitionsResult.status === 'fulfilled') {
        setCompetitions(competitionsResult.value);
      } else {
        setCompetitions([]);
        setCompetitionsError(errorMessage(competitionsResult.reason, 'competition details'));
      }

      setLoading(false);
    };

    void loadDashboard();
    return () => { isCurrent = false; };
  }, [retryCount]);

  const competitionById = useMemo(
    () => new Map(competitions.map((competition) => [competition.id, competition])),
    [competitions],
  );
  const retry = () => setRetryCount((count) => count + 1);

  return (
    <div className="content-page athlete-dashboard-page">
      <header className="athlete-dashboard-hero">
        <div className="athlete-dashboard-hero-copy">
          <div className="section-label">ATHLETE · OVERVIEW</div>
          <h1>{athlete ? `Welcome, ${athlete.name.split(' ')[0]}` : 'Athlete Dashboard'}</h1>
          <p>Your profile and registered competitions, together in one place.</p>
        </div>
        <div className="athlete-dashboard-hero-mark" aria-hidden="true">❄</div>
      </header>

      {loading && (
        <div className="athlete-dashboard-loading" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading your athlete dashboard...</p>
        </div>
      )}

      {!loading && (
        <>
          {profileError && <LoadError message={profileError} retry={retry} />}

          {athlete && (
            <section className="athlete-dashboard-profile" aria-labelledby="dashboard-profile-title">
              <div className="athlete-dashboard-profile-heading">
                <span className="athlete-dashboard-emblem" aria-hidden="true">♙</span>
                <div>
                  <span className="athlete-dashboard-eyebrow">YOUR ATHLETE PROFILE</span>
                  <h2 id="dashboard-profile-title">{athlete.name}</h2>
                </div>
                <Link className="athlete-dashboard-link-button" to="/athlete/profile">View My Profile <span aria-hidden="true">→</span></Link>
              </div>
              <dl className="athlete-dashboard-profile-details">
                <div><dt>Country</dt><dd>{athlete.country}</dd></div>
                <div><dt>Gender</dt><dd>{athlete.gender === 'MALE' ? 'Male' : 'Female'}</dd></div>
                <div><dt>Date of birth</dt><dd>{formatDate(athlete.dateOfBirth)}</dd></div>
              </dl>
            </section>
          )}

          <section className="athlete-dashboard-section" aria-labelledby="dashboard-competitions-title">
            <div className="athlete-dashboard-section-heading">
              <div><span className="athlete-dashboard-eyebrow">YOUR EVENTS</span><h2 id="dashboard-competitions-title">My Competitions</h2></div>
              <Link to="/athlete/competitions" className="athlete-dashboard-text-link">Manage registrations <span aria-hidden="true">→</span></Link>
            </div>

            {registrationsError && <LoadError message={registrationsError} retry={retry} />}
            {!registrationsError && registrations.length === 0 && (
              <div className="athlete-dashboard-empty">
                <span aria-hidden="true">❄</span>
                <h3>No registered competitions yet</h3>
                <p>Browse the event program and register for a competition that suits you.</p>
                <Link to="/athlete/competitions" className="athlete-dashboard-link-button">Explore competitions <span aria-hidden="true">→</span></Link>
              </div>
            )}
            {!registrationsError && registrations.length > 0 && (
              <div className="athlete-dashboard-competition-grid">
                {registrations.map((registration) => {
                  const competition = competitionById.get(registration.competitionId);
                  return (
                    <article className="athlete-dashboard-competition-card" key={registration.id}>
                      <div className="athlete-dashboard-card-topline">
                        <span className="athlete-dashboard-type-pill">{disciplineLabel(competition?.type)}</span>
                        <span className="athlete-dashboard-registered"><span aria-hidden="true">●</span> Registered</span>
                      </div>
                      <h3>{registration.competitionName}</h3>
                      <p>{genderLabel(competition?.gender)}</p>
                    </article>
                  );
                })}
              </div>
            )}
            {competitionsError && <p className="athlete-dashboard-info" role="status">Competition type and gender could not be loaded. Retry to refresh those details.</p>}
          </section>

          <section className="athlete-dashboard-actions" aria-labelledby="dashboard-actions-title">
            <div className="athlete-dashboard-section-heading">
              <div><span className="athlete-dashboard-eyebrow">KEEP MOVING</span><h2 id="dashboard-actions-title">Quick Actions</h2></div>
            </div>
            <div className="athlete-dashboard-action-grid">
              <Link to="/athlete/profile" className="athlete-dashboard-action-card"><span aria-hidden="true">♙</span><strong>My Profile</strong><small>Review or update your athlete information</small><b aria-hidden="true">→</b></Link>
              <Link to="/athlete/competitions" className="athlete-dashboard-action-card"><span aria-hidden="true">❄</span><strong>My Competitions</strong><small>Manage your event registrations</small><b aria-hidden="true">→</b></Link>
              <Link to="/competitions" className="athlete-dashboard-action-card"><span aria-hidden="true">✦</span><strong>Browse Competitions</strong><small>Explore the Winter Olympics program</small><b aria-hidden="true">→</b></Link>
            </div>
          </section>
        </>
      )}
    </div>
  );
};
