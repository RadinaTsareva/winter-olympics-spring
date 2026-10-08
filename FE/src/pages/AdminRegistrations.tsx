import { useEffect, useMemo, useState } from 'react';
import { ApiError } from '@services/apiClient';
import { getRegistrations, unregisterFromCompetition } from '@services/registrationApi';
import type { CompetitionRegistration } from '../types';
import '@styles/content-page.css';
import '@styles/admin-registrations.css';

function describeError(error: unknown): string {
  if (error instanceof ApiError) return error.message;
  if (error instanceof Error && (error.message.toLowerCase().includes('fetch') || error.message.toLowerCase().includes('network'))) {
    return 'Could not connect to the server. Check your connection and try again.';
  }
  return error instanceof Error ? error.message : 'Something went wrong. Please try again.';
}

export const AdminRegistrations: React.FC = () => {
  const [registrations, setRegistrations] = useState<CompetitionRegistration[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [retry, setRetry] = useState(0);
  const [search, setSearch] = useState('');
  const [competitionFilter, setCompetitionFilter] = useState('all');
  const [selectedRegistration, setSelectedRegistration] = useState<CompetitionRegistration | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError(null);
    getRegistrations().then((data) => {
      if (active) setRegistrations(data);
    }).catch((error: unknown) => {
      if (active) setLoadError(describeError(error));
    }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [retry]);

  const competitions = useMemo(() => [...new Set(registrations.map((item) => item.competitionName))].sort((a, b) => a.localeCompare(b)), [registrations]);
  const visibleRegistrations = useMemo(() => registrations.filter((registration) => {
    const matchesName = registration.athleteName.toLocaleLowerCase().includes(search.trim().toLocaleLowerCase());
    const matchesCompetition = competitionFilter === 'all' || registration.competitionName === competitionFilter;
    return matchesName && matchesCompetition;
  }), [registrations, search, competitionFilter]);

  const refreshRegistrations = async () => {
    const data = await getRegistrations();
    setRegistrations(data);
    setLoadError(null);
  };

  const confirmDelete = async () => {
    if (!selectedRegistration || deletingId !== null) return;
    const registration = selectedRegistration;
    setDeletingId(registration.id);
    setActionError(null);
    setSuccess(null);
    try {
      await unregisterFromCompetition(registration.id);
      setSelectedRegistration(null);
      setSuccess(`Registration #${registration.id} for ${registration.athleteName} was deleted.`);
      try {
        await refreshRegistrations();
      } catch (error) {
        setLoadError(`The registration was deleted, but the list could not be refreshed. ${describeError(error)}`);
      }
    } catch (error) {
      // Preserve the backend message so administrators can see the actual constraint or validation failure.
      setActionError(describeError(error));
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="content-page admin-registrations-page">
      <div className="page-header">
        <div className="section-label">ADMIN · MANAGEMENT</div>
        <h1>Manage Registrations</h1>
        <p>Review athlete entries across the Winter Olympics competitions.</p>
      </div>

      <section className="admin-registration-panel" aria-label="Registration management">
        <div className="admin-registration-toolbar">
          <div><span className="admin-registration-kicker">REGISTRATION DIRECTORY</span><h2>Registrations <span>{loading ? '…' : registrations.length}</span></h2></div>
          <div className="admin-registration-filters">
            <label className="admin-registration-search"><span className="sr-only">Search by athlete name</span><span aria-hidden="true">⌕</span><input type="search" placeholder="Search athlete" value={search} onChange={(event) => setSearch(event.target.value)} /></label>
            <label className="sr-only" htmlFor="registration-competition-filter">Filter by competition</label>
            <select id="registration-competition-filter" value={competitionFilter} onChange={(event) => setCompetitionFilter(event.target.value)}><option value="all">All competitions</option>{competitions.map((competition) => <option key={competition} value={competition}>{competition}</option>)}</select>
          </div>
        </div>

        {success && <div className="admin-registration-success" role="status">{success}</div>}
        {loadError && <div className="admin-registration-error-banner" role="alert"><span>{loadError}</span><button type="button" onClick={() => setRetry((count) => count + 1)}>Retry</button></div>}
        {loading && <div className="admin-registration-state" role="status"><span className="admin-registration-spinner" />Loading registrations...</div>}
        {!loading && loadError && registrations.length === 0 && <div className="admin-registration-state admin-registration-error-state" role="alert"><h3>Could not load registrations</h3><p>{loadError}</p><button type="button" onClick={() => setRetry((count) => count + 1)}>Retry</button></div>}
        {!loading && !loadError && registrations.length === 0 && <div className="admin-registration-state admin-registration-empty"><span aria-hidden="true">▤</span><h3>No registrations yet</h3><p>Athlete competition registrations will appear here.</p></div>}
        {!loading && registrations.length > 0 && visibleRegistrations.length === 0 && <div className="admin-registration-state admin-registration-empty"><span aria-hidden="true">⌕</span><h3>No matching registrations</h3><p>Try another athlete name or competition filter.</p><button type="button" onClick={() => { setSearch(''); setCompetitionFilter('all'); }}>Clear filters</button></div>}
        {!loading && visibleRegistrations.length > 0 && <div className="admin-registration-table-wrap"><table className="admin-registration-table"><thead><tr><th scope="col">Registration ID</th><th scope="col">Athlete</th><th scope="col">Competition</th><th scope="col">Competition ID</th><th scope="col">Athlete ID</th><th scope="col">Actions</th></tr></thead><tbody>{visibleRegistrations.map((registration) => <tr key={registration.id}><td><span className="admin-registration-id">#{registration.id}</span></td><th scope="row">{registration.athleteName}</th><td>{registration.competitionName}</td><td><span className="admin-reference-id">{registration.competitionId}</span></td><td><span className="admin-reference-id">{registration.athleteId}</span></td><td><button className="admin-registration-delete" type="button" onClick={() => { setSelectedRegistration(registration); setActionError(null); setSuccess(null); }} disabled={deletingId !== null}>Delete</button></td></tr>)}</tbody></table></div>}
      </section>

      {selectedRegistration && <div className="admin-registration-modal-backdrop"><section className="admin-registration-dialog" role="alertdialog" aria-modal="true" aria-labelledby="registration-delete-title" aria-describedby="registration-delete-description"><span className="admin-registration-warning" aria-hidden="true">!</span><h2 id="registration-delete-title">Permanently remove this registration?</h2><p id="registration-delete-description">Registration <strong>#{selectedRegistration.id}</strong> for <strong>{selectedRegistration.athleteName}</strong> in <strong>{selectedRegistration.competitionName}</strong> will be permanently removed.</p>{actionError && <div className="admin-registration-error-banner" role="alert">{actionError}</div>}<div className="admin-registration-dialog-actions"><button className="admin-registration-cancel" type="button" onClick={() => setSelectedRegistration(null)} disabled={deletingId !== null}>Keep Registration</button><button className="admin-registration-confirm" type="button" onClick={() => void confirmDelete()} disabled={deletingId !== null}>{deletingId === selectedRegistration.id ? 'Deleting...' : 'Delete Registration'}</button></div></section></div>}
    </div>
  );
};
