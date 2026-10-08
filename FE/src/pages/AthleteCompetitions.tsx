import { useEffect, useState } from 'react';
import { ApiError } from '@services/apiClient';
import { getMyAthleteProfile } from '@services/athleteApi';
import { getCompetitions } from '@services/competitionApi';
import { getRegistrations, registerForCompetition, unregisterFromCompetition } from '@services/registrationApi';
import type { Competition, CompetitionRegistration, AthleteProfileData } from '../types';
import '@styles/content-page.css';
import '@styles/athlete-competitions.css';

const friendlyError = (error: unknown) => {
  if (error instanceof ApiError) {
    if (error.status === 401 || error.status === 403) return 'Your session is not authorized to manage these registrations. Please sign in with your athlete account.';
    const message = error.message.toLowerCase();
    if (message.includes('already registered')) return 'You are already registered for this competition.';
    if (message.includes('gender')) return 'Your profile gender does not match this competition.';
    if (message.includes('minimum age') || message.includes('age requirement')) return 'You do not meet the minimum age requirement for this competition.';
    return error.message;
  }
  return error instanceof Error && error.message.toLowerCase().includes('fetch')
    ? 'Could not connect to the server. Check your connection and try again.'
    : 'Something went wrong. Please try again.';
};

const typeLabel = (type: Competition['type'] | undefined) => type === 'BIATHLON' ? 'Biathlon' : type === 'SKI_SLALOM' ? 'Ski Slalom' : 'Competition';
const genderLabel = (gender: Competition['gender'] | undefined) => gender === 'MALE' ? "Men's event" : gender === 'FEMALE' ? "Women's event" : 'Gender unavailable';

export const AthleteCompetitions: React.FC = () => {
  const [athlete, setAthlete] = useState<AthleteProfileData | null>(null);
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [registrations, setRegistrations] = useState<CompetitionRegistration[]>([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState<string | null>(null);
  const [retryCount, setRetryCount] = useState(0);
  const [activeAction, setActiveAction] = useState<{ kind: 'register' | 'unregister'; id: number } | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [registrationToRemove, setRegistrationToRemove] = useState<CompetitionRegistration | null>(null);

  useEffect(() => {
    let isCurrent = true;

    const loadPage = async () => {
      setLoading(true);
      setPageError(null);
      try {
        // Resolve ownership from the authenticated account before loading page data.
        const currentAthlete = await getMyAthleteProfile();
        if (!isCurrent) return;
        setAthlete(currentAthlete);

        const [availableCompetitions, currentRegistrations] = await Promise.all([
          getCompetitions(),
          getRegistrations(),
        ]);
        if (isCurrent) {
          setCompetitions(availableCompetitions);
          setRegistrations(currentRegistrations);
        }
      } catch (error) {
        if (isCurrent) setPageError(friendlyError(error));
      } finally {
        if (isCurrent) setLoading(false);
      }
    };

    void loadPage();
    return () => { isCurrent = false; };
  }, [retryCount]);

  const registeredCompetitionIds = new Set(registrations.map((registration) => registration.competitionId));
  const availableCompetitions = competitions.filter((competition) => !registeredCompetitionIds.has(competition.id));
  const competitionById = new Map(competitions.map((competition) => [competition.id, competition]));

  const handleRegister = async (competition: Competition) => {
    if (!athlete || activeAction) return;
    setActionError(null);
    setSuccessMessage(null);
    setActiveAction({ kind: 'register', id: competition.id });
    try {
      const newRegistration = await registerForCompetition(athlete.id, competition.id);
      setRegistrations((current) => current.some((item) => item.id === newRegistration.id)
        ? current
        : [...current, newRegistration]);
      setSuccessMessage(`${competition.name} was added to your competitions.`);

      try {
        setRegistrations(await getRegistrations());
      } catch {
        setActionError('Registration succeeded, but the list could not be refreshed. The new registration is shown below.');
      }
    } catch (error) {
      setActionError(friendlyError(error));
    } finally {
      setActiveAction(null);
    }
  };

  const handleUnregister = async () => {
    if (!registrationToRemove || activeAction) return;
    const registration = registrationToRemove;
    setActionError(null);
    setSuccessMessage(null);
    setActiveAction({ kind: 'unregister', id: registration.id });
    try {
      await unregisterFromCompetition(registration.id);
      setRegistrations((current) => current.filter((item) => item.id !== registration.id));
      setRegistrationToRemove(null);
      setSuccessMessage(`${registration.competitionName} was removed from your competitions.`);

      try {
        setRegistrations(await getRegistrations());
      } catch {
        setActionError('Unregistration succeeded, but the list could not be refreshed. The competition is shown as available.');
      }
    } catch (error) {
      setActionError(friendlyError(error));
    } finally {
      setActiveAction(null);
    }
  };

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">ATHLETE · COMPETITIONS</div>
        <h1>My Competitions</h1>
        <p>Manage your registrations and discover available events.</p>
      </div>

      {loading && (
        <div className="athlete-competitions-state" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading your competitions...</p>
        </div>
      )}

      {!loading && pageError && (
        <div className="athlete-competitions-state athlete-competitions-error" role="alert">
          <h2>Could not load your competitions</h2>
          <p>{pageError}</p>
          <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>Retry</button>
        </div>
      )}

      {!loading && !pageError && athlete && (
        <div className="athlete-competitions-content">
          {successMessage && <div className="athlete-competitions-success" role="status">{successMessage}</div>}
          {actionError && <div className="athlete-competitions-error-message" role="alert">{actionError}</div>}

          <section className="athlete-competition-section" aria-labelledby="registered-competitions-title">
            <div className="athlete-competition-section-heading">
              <div>
                <div className="athlete-competition-eyebrow">YOUR EVENTS</div>
                <h2 id="registered-competitions-title">My Registered Competitions</h2>
              </div>
              <span className="athlete-competition-count">{registrations.length} registered</span>
            </div>

            {registrations.length === 0 ? (
              <div className="athlete-competition-empty">
                <span aria-hidden="true">❄</span>
                <h3>No registered competitions</h3>
                <p>Choose an available event below to get started.</p>
              </div>
            ) : (
              <div className="athlete-competition-grid">
                {registrations.map((registration) => {
                  const competition = competitionById.get(registration.competitionId);
                  const isUnregistering = activeAction?.kind === 'unregister' && activeAction.id === registration.id;
                  return (
                    <article className="athlete-competition-card registered-card" key={registration.id}>
                      <div className="athlete-competition-card-topline">
                        <span className="competition-type">{typeLabel(competition?.type)}</span>
                        <span className="registration-status"><span aria-hidden="true">●</span> Registered</span>
                      </div>
                      <h3>{registration.competitionName}</h3>
                      <p className="athlete-competition-meta">{genderLabel(competition?.gender)}</p>
                      <button
                        className="unregister-button"
                        type="button"
                        onClick={() => { setActionError(null); setSuccessMessage(null); setRegistrationToRemove(registration); }}
                        disabled={Boolean(activeAction)}
                      >
                        {isUnregistering ? 'Unregistering...' : 'Unregister'}
                      </button>
                    </article>
                  );
                })}
              </div>
            )}
          </section>

          <section className="athlete-competition-section" aria-labelledby="available-competitions-title">
            <div className="athlete-competition-section-heading">
              <div>
                <div className="athlete-competition-eyebrow">EXPLORE EVENTS</div>
                <h2 id="available-competitions-title">Available Competitions</h2>
              </div>
              <span className="athlete-competition-count">{availableCompetitions.length} available</span>
            </div>

            {availableCompetitions.length === 0 ? (
              <div className="athlete-competition-empty">
                <span aria-hidden="true">✦</span>
                <h3>No available competitions</h3>
                <p>You are registered for every listed competition.</p>
              </div>
            ) : (
              <div className="athlete-competition-grid">
                {availableCompetitions.map((competition) => {
                  const isRegistering = activeAction?.kind === 'register' && activeAction.id === competition.id;
                  return (
                    <article className="athlete-competition-card available-card" key={competition.id}>
                      <div className="athlete-competition-card-topline">
                        <span className="competition-type">{typeLabel(competition.type)}</span>
                        <span className="competition-min-age">Minimum age {competition.minimumAge}</span>
                      </div>
                      <h3>{competition.name}</h3>
                      <p className="athlete-competition-meta">{genderLabel(competition.gender)}</p>
                      {competition.type === 'BIATHLON' && (
                        <div className="athlete-biathlon-details">
                          <div><span>Number of laps</span><strong>{competition.numberOfLaps ?? '—'}</strong></div>
                          <div><span>Shooting after lap</span><strong>{competition.shootingAfterLaps ?? '—'}</strong></div>
                        </div>
                      )}
                      <button
                        className="register-button"
                        type="button"
                        onClick={() => void handleRegister(competition)}
                        disabled={Boolean(activeAction)}
                      >
                        {isRegistering ? <><span className="auth-button-spinner" aria-hidden="true" /> Registering...</> : 'Register'}
                      </button>
                    </article>
                  );
                })}
              </div>
            )}
          </section>
        </div>
      )}

      {registrationToRemove && (
        <div className="athlete-registration-dialog-backdrop">
          <section className="athlete-registration-dialog" role="alertdialog" aria-modal="true" aria-labelledby="unregister-title" aria-describedby="unregister-description">
            <span className="athlete-registration-dialog-icon" aria-hidden="true">?</span>
            <h2 id="unregister-title">Unregister from this competition?</h2>
            <p id="unregister-description">You will be removed from <strong>{registrationToRemove.competitionName}</strong>. You can register again later if places are available.</p>
            {actionError && <div className="athlete-competitions-error-message" role="alert">{actionError}</div>}
            {activeAction?.kind === 'unregister' && <p className="unregister-progress" role="status">Unregistering...</p>}
            <div className="athlete-registration-dialog-actions">
              <button className="athlete-dialog-cancel" type="button" onClick={() => setRegistrationToRemove(null)} disabled={Boolean(activeAction)}>Keep registration</button>
              <button className="athlete-dialog-confirm" type="button" onClick={() => void handleUnregister()} disabled={Boolean(activeAction)}>
                {activeAction?.kind === 'unregister' ? 'Unregistering...' : 'Confirm unregister'}
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
};
