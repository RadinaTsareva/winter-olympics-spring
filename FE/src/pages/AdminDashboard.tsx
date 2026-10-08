import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAthletes } from '@services/athleteApi';
import { getCompetitions } from '@services/competitionApi';
import { getRegistrations } from '@services/registrationApi';
import { createTestData, type TestDataSummary } from '@services/testDataApi';
import type { AthleteProfileData, Competition, CompetitionRegistration } from '../types';
import '@styles/content-page.css';
import '@styles/admin-dashboard.css';

type LoadState<T> = { data: T | null; loading: boolean; error: string | null; retry: number };

function useDashboardData<T>(load: () => Promise<T>, label: string): [LoadState<T>, () => void] {
  const [state, setState] = useState<LoadState<T>>({ data: null, loading: true, error: null, retry: 0 });
  useEffect(() => {
    let current = true;
    setState((previous) => ({ ...previous, loading: true, error: null }));
    load().then((data) => {
      if (current) setState((previous) => ({ ...previous, data, loading: false, error: null }));
    }).catch((error: unknown) => {
      if (current) setState((previous) => ({
        ...previous,
        loading: false,
        error: error instanceof Error ? error.message : `Could not load ${label.toLowerCase()}.`,
      }));
    });
    return () => { current = false; };
  }, [load, label, state.retry]);
  const retry = () => setState((previous) => ({ ...previous, retry: previous.retry + 1 }));
  return [state, retry];
}

const loadAthletes = () => getAthletes();
const loadCompetitions = () => getCompetitions();
const loadRegistrations = () => getRegistrations();

const actions = [
  { title: 'Manage Athletes', description: 'Review athlete profiles and records.', to: '/admin/athletes', icon: '♙' },
  { title: 'Manage Competitions', description: 'Review events and competition setup.', to: '/admin/competitions', icon: '❄' },
  { title: 'Registrations', description: 'View athlete competition registrations.', to: '/admin/registrations', icon: '▤' },
  { title: 'Slalom Results', description: 'Manage ski slalom event results.', to: '/admin/slalom-results', icon: '⌁' },
  { title: 'Biathlon Results', description: 'Manage biathlon event results.', to: '/admin/biathlon-results', icon: '◎' },
];

const typeLabel = (type: Competition['type']) => type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom';
const genderLabel = (gender: Competition['gender']) => gender === 'MALE' ? "Men's event" : "Women's event";

function retryBlock(error: string, retry: () => void) {
  return <div className="admin-dashboard-error" role="alert"><p>{error}</p><button type="button" onClick={retry}>Retry</button></div>;
}

function countValue<T>(state: LoadState<T[]>, label: string, retry: () => void) {
  if (state.loading) return <span className="admin-count-loading" aria-label={`Loading ${label}`} />;
  if (state.error) return <span className="admin-count-error">Unavailable <button type="button" onClick={retry}>Retry</button></span>;
  return state.data?.length ?? 0;
}

function emptyHint<T>(state: LoadState<T[]>, message: string) {
  return !state.loading && !state.error && state.data?.length === 0 ? <small className="admin-empty-hint">{message}</small> : null;
}

export const AdminDashboard: React.FC = () => {
  const [athletes, retryAthletes] = useDashboardData<AthleteProfileData[]>(loadAthletes, 'Athletes');
  const [competitions, retryCompetitions] = useDashboardData<Competition[]>(loadCompetitions, 'Competitions');
  const [registrations, retryRegistrations] = useDashboardData<CompetitionRegistration[]>(loadRegistrations, 'Registrations');
  const [confirmTestData, setConfirmTestData] = useState(false);
  const [seeding, setSeeding] = useState(false);
  const [testDataError, setTestDataError] = useState<string | null>(null);
  const [testDataSummary, setTestDataSummary] = useState<TestDataSummary | null>(null);

  const handleCreateTestData = async () => {
    if (seeding) return;
    setSeeding(true);
    setTestDataError(null);
    setTestDataSummary(null);
    try {
      const summary = await createTestData();
      setTestDataSummary(summary);
      setConfirmTestData(false);
      retryAthletes();
      retryCompetitions();
      retryRegistrations();
    } catch (error) {
      setTestDataError(error instanceof Error ? error.message : 'Could not create test data. Please try again.');
    } finally {
      setSeeding(false);
    }
  };

  return (
    <div className="content-page admin-dashboard">
      <div className="page-header">
        <div className="section-label">ADMIN · OVERVIEW</div>
        <h1>Dashboard</h1>
        <p>A clear view of athletes, events, and registrations across the Winter Olympics.</p>
      </div>

      <section className="admin-summary-grid" aria-label="Olympics overview">
        <article className="admin-summary-card"><span className="admin-summary-icon">♙</span><span className="admin-summary-label">Total Athletes</span><strong>{countValue(athletes, 'athletes', retryAthletes)}</strong>{emptyHint(athletes, 'No athletes registered yet.')}</article>
        <article className="admin-summary-card"><span className="admin-summary-icon">❄</span><span className="admin-summary-label">Total Competitions</span><strong>{countValue(competitions, 'competitions', retryCompetitions)}</strong>{emptyHint(competitions, 'No competitions are available.')}</article>
        <article className="admin-summary-card"><span className="admin-summary-icon">▤</span><span className="admin-summary-label">Total Registrations</span><strong>{countValue(registrations, 'registrations', retryRegistrations)}</strong>{emptyHint(registrations, 'No registrations have been submitted.')}</article>
      </section>

      <section className="admin-dashboard-section" aria-labelledby="admin-competitions-heading">
        <div className="admin-section-heading">
          <div><span className="admin-section-kicker">EVENT PROGRAM</span><h2 id="admin-competitions-heading">Competitions</h2></div>
          <Link to="/admin/competitions" className="admin-section-link">Manage competitions <span aria-hidden="true">→</span></Link>
        </div>
        {competitions.loading && <div className="admin-section-state" role="status"><span className="admin-spinner" />Loading competitions...</div>}
        {!competitions.loading && competitions.error && retryBlock(competitions.error, retryCompetitions)}
        {!competitions.loading && !competitions.error && competitions.data?.length === 0 && <div className="admin-section-state admin-empty"><span aria-hidden="true">❄</span><h3>No competitions yet</h3><p>Competitions will appear here when they are available.</p></div>}
        {!competitions.loading && !competitions.error && Boolean(competitions.data?.length) && (
          <div className="admin-competition-grid">
            {competitions.data?.map((competition) => (
              <article className="admin-competition-card" key={competition.id}>
                <div className="admin-competition-topline"><span className="admin-type-pill">{typeLabel(competition.type)}</span><span>Minimum age {competition.minimumAge}</span></div>
                <h3>{competition.name}</h3>
                <p>{genderLabel(competition.gender)}</p>
                {competition.type === 'BIATHLON' && <div className="admin-biathlon-details"><span><strong>{competition.numberOfLaps ?? '—'}</strong> laps</span><span>Shooting after lap <strong>{competition.shootingAfterLaps ?? '—'}</strong></span></div>}
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="admin-dashboard-section" aria-labelledby="admin-actions-heading">
        <div className="admin-section-heading"><div><span className="admin-section-kicker">QUICK LINKS</span><h2 id="admin-actions-heading">Admin Actions</h2></div></div>
        <div className="admin-actions-grid">
          {actions.map((action) => <Link to={action.to} className="admin-action-card" key={action.to}><span className="admin-action-icon" aria-hidden="true">{action.icon}</span><span className="admin-action-copy"><strong>{action.title}</strong><small>{action.description}</small></span><span className="admin-action-arrow" aria-hidden="true">→</span></Link>)}
        </div>
      </section>

      <section className="admin-dashboard-section admin-demo-data-section" aria-labelledby="admin-demo-data-heading">
        <div className="admin-section-heading"><div><span className="admin-section-kicker">REUSABLE DEMONSTRATION DATA</span><h2 id="admin-demo-data-heading">Demo / Test Data</h2></div></div>
        <div className="admin-demo-data-card">
          <div className="admin-demo-data-copy"><strong>Create a complete Winter Olympics demo dataset</strong><p>This creates reusable demonstration data. Existing unrelated data will not be deleted.</p></div>
          <button className="admin-demo-data-button" type="button" onClick={() => { setTestDataError(null); setConfirmTestData(true); }} disabled={seeding}>{seeding ? 'Creating test data...' : 'Create Test Data'}</button>
        </div>
        {testDataError && <div className="admin-demo-data-error" role="alert"><strong>Test data was not created</strong><span>{testDataError}</span></div>}
        {testDataSummary && <div className="admin-demo-data-success" role="status"><strong>{testDataSummary.message}</strong><div className="admin-seed-count-grid">{[
          ['Countries', testDataSummary.countriesCreated, testDataSummary.countriesReused],
          ['Athletes', testDataSummary.athletesCreated, testDataSummary.athletesReused],
          ['Competitions', testDataSummary.competitionsCreated, testDataSummary.competitionsReused],
          ['Registrations', testDataSummary.registrationsCreated, testDataSummary.registrationsReused],
          ['Slalom results', testDataSummary.slalomResultsCreated, testDataSummary.slalomResultsReused],
          ['Biathlon results', testDataSummary.biathlonResultsCreated, testDataSummary.biathlonResultsReused],
        ].map(([label, created, reused]) => <span key={label}><b>{label}</b>{created} created · {reused} reused</span>)}</div></div>}
      </section>

      {confirmTestData && <div className="admin-seed-modal-backdrop"><section className="admin-seed-dialog" role="alertdialog" aria-modal="true" aria-labelledby="admin-seed-title" aria-describedby="admin-seed-description"><span className="admin-seed-dialog-icon" aria-hidden="true">❄</span><h2 id="admin-seed-title">Create reusable test data?</h2><p id="admin-seed-description">This will add the demo countries, athletes, competitions, registrations, and results where they are missing. Existing matching demo data will be reused. Unrelated data will not be deleted or changed.</p>{testDataError && <div className="admin-demo-data-error" role="alert">{testDataError}</div>}<div className="admin-seed-dialog-actions"><button className="admin-seed-cancel" type="button" onClick={() => setConfirmTestData(false)} disabled={seeding}>Cancel</button><button className="admin-demo-data-button" type="button" onClick={() => void handleCreateTestData()} disabled={seeding}>{seeding ? 'Creating...' : 'Confirm and Create'}</button></div></section></div>}
    </div>
  );
};
