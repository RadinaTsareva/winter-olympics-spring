import { useEffect, useState, type FormEvent } from 'react';
import { ApiError } from '@services/apiClient';
import { createCompetition, deleteCompetition, getCompetitions, updateCompetition, type CompetitionRequest } from '@services/competitionApi';
import type { Competition } from '../types';
import '@styles/content-page.css';
import '@styles/admin-competitions.css';

type CompetitionForm = { name: string; type: Competition['type']; gender: Competition['gender']; minimumAge: string; numberOfLaps: string; shootingAfterLaps: string };
const emptyForm: CompetitionForm = { name: '', type: 'SKI_SLALOM', gender: 'MALE', minimumAge: '', numberOfLaps: '', shootingAfterLaps: '' };

function describeError(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.status === 401 || error.status === 403) return 'Your admin session is not authorized for this action. Sign in with an administrator account and try again.';
    return error.message;
  }
  if (error instanceof Error && (error.message.toLowerCase().includes('fetch') || error.message.toLowerCase().includes('network'))) return 'Could not connect to the server. Check your connection and try again.';
  return error instanceof Error ? error.message : 'Something went wrong. Please try again.';
}

const typeLabel = (type: Competition['type']) => type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom';
const genderLabel = (gender: Competition['gender']) => gender === 'MALE' ? "Men's event" : "Women's event";

export const AdminCompetitions: React.FC = () => {
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [retry, setRetry] = useState(0);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Competition | null>(null);
  const [form, setForm] = useState<CompetitionForm>(emptyForm);
  const [formError, setFormError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Competition | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError(null);
    getCompetitions().then((data) => { if (active) setCompetitions(data); })
      .catch((error: unknown) => { if (active) setLoadError(describeError(error)); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [retry]);

  const refresh = async () => {
    const data = await getCompetitions();
    setCompetitions(data);
    setLoadError(null);
  };

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setFormError(null);
    setSuccess(null);
    setFormOpen(true);
  };

  const openEdit = (competition: Competition) => {
    setEditing(competition);
    setForm({
      name: competition.name,
      type: competition.type,
      gender: competition.gender,
      minimumAge: String(competition.minimumAge),
      numberOfLaps: competition.numberOfLaps === null ? '' : String(competition.numberOfLaps),
      shootingAfterLaps: competition.shootingAfterLaps === null ? '' : String(competition.shootingAfterLaps),
    });
    setFormError(null);
    setSuccess(null);
    setFormOpen(true);
  };

  const changeType = (type: Competition['type']) => {
    setForm((current) => ({ ...current, type, numberOfLaps: type === 'BIATHLON' ? current.numberOfLaps : '', shootingAfterLaps: type === 'BIATHLON' ? current.shootingAfterLaps : '' }));
  };

  const handleSave = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setSuccess(null);
    const name = form.name.trim();
    const minimumAge = Number(form.minimumAge);
    if (!name) { setFormError('Enter a competition name.'); return; }
    if (!Number.isInteger(minimumAge) || minimumAge < 0) { setFormError('Minimum age must be a whole number of 0 or greater.'); return; }

    let numberOfLaps: number | null = null;
    let shootingAfterLaps: number | null = null;
    if (form.type === 'BIATHLON') {
      numberOfLaps = Number(form.numberOfLaps);
      shootingAfterLaps = Number(form.shootingAfterLaps);
      if (!form.numberOfLaps || !Number.isInteger(numberOfLaps) || numberOfLaps < 1) { setFormError('Number of laps is required and must be a whole number greater than 0.'); return; }
      if (!form.shootingAfterLaps || !Number.isInteger(shootingAfterLaps) || shootingAfterLaps < 1) { setFormError('Shooting after laps is required and must be a whole number greater than 0.'); return; }
      if (shootingAfterLaps > numberOfLaps) { setFormError('Shooting after laps cannot be greater than the total number of laps.'); return; }
    }

    const payload: CompetitionRequest = { name, type: form.type, gender: form.gender, minimumAge, numberOfLaps, shootingAfterLaps };
    setSaving(true);
    try {
      if (editing) {
        await updateCompetition(editing.id, payload);
        setSuccess(`${name} was updated successfully.`);
      } else {
        await createCompetition(payload);
        setSuccess(`${name} was created successfully.`);
      }
      setFormOpen(false);
      try { await refresh(); }
      catch (error) { setLoadError(`The change succeeded, but the competition list could not be refreshed. ${describeError(error)}`); }
    } catch (error) {
      setFormError(describeError(error));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget || deletingId !== null) return;
    const target = deleteTarget;
    setDeletingId(target.id);
    setDeleteError(null);
    setSuccess(null);
    try {
      await deleteCompetition(target.id);
      setDeleteTarget(null);
      setSuccess(`${target.name} was deleted successfully.`);
      try { await refresh(); }
      catch (error) { setLoadError(`The competition was deleted, but the list could not be refreshed. ${describeError(error)}`); }
    } catch (error) {
      setDeleteError(describeError(error));
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="content-page admin-competitions-page">
      <div className="page-header"><div className="section-label">ADMIN · MANAGEMENT</div><h1>Manage Competitions</h1><p>Create, review, and maintain Winter Olympics events.</p></div>

      <section className="admin-competitions-panel" aria-label="Competition management">
        <div className="admin-competitions-toolbar"><div><span className="admin-competitions-kicker">EVENT PROGRAM</span><h2>Competitions <span>{loading ? '…' : competitions.length}</span></h2></div><button className="admin-competition-primary" type="button" onClick={openCreate}>＋ Create Competition</button></div>
        {success && <div className="admin-competition-success" role="status">{success}</div>}
        {loadError && <div className="admin-competition-error-banner" role="alert"><span>{loadError}</span><button type="button" onClick={() => setRetry((value) => value + 1)}>Retry</button></div>}
        {loading && <div className="admin-competition-state" role="status"><span className="admin-competition-spinner" />Loading competitions...</div>}
        {!loading && loadError && competitions.length === 0 && <div className="admin-competition-state admin-competition-error-state" role="alert"><h3>Could not load competitions</h3><p>{loadError}</p><button type="button" onClick={() => setRetry((value) => value + 1)}>Retry</button></div>}
        {!loading && !loadError && competitions.length === 0 && <div className="admin-competition-state admin-competition-empty"><span aria-hidden="true">❄</span><h3>No competitions yet</h3><p>Create an event to add it to the program.</p></div>}
        {!loading && competitions.length > 0 && <div className="admin-competition-table-wrap"><table className="admin-competition-table"><thead><tr><th scope="col">Name</th><th scope="col">Type</th><th scope="col">Gender</th><th scope="col">Minimum age</th><th scope="col">Laps</th><th scope="col">Shooting after lap</th><th scope="col">Actions</th></tr></thead><tbody>{competitions.map((competition) => <tr key={competition.id}><th scope="row">{competition.name}</th><td><span className="admin-competition-type-tag">{typeLabel(competition.type)}</span></td><td>{genderLabel(competition.gender)}</td><td>{competition.minimumAge}+</td><td>{competition.type === 'BIATHLON' ? competition.numberOfLaps ?? '—' : '—'}</td><td>{competition.type === 'BIATHLON' ? competition.shootingAfterLaps ?? '—' : '—'}</td><td><div className="admin-competition-actions"><button type="button" onClick={() => openEdit(competition)}>Edit</button><button className="admin-competition-delete" type="button" onClick={() => { setDeleteError(null); setDeleteTarget(competition); }}>Delete</button></div></td></tr>)}</tbody></table></div>}
      </section>

      {formOpen && <div className="admin-competition-modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget && !saving) setFormOpen(false); }}><section className="admin-competition-modal" role="dialog" aria-modal="true" aria-labelledby="admin-competition-form-title"><div className="admin-competition-modal-heading"><div><span className="admin-competitions-kicker">EVENT DETAILS</span><h2 id="admin-competition-form-title">{editing ? 'Edit Competition' : 'Create Competition'}</h2></div><button className="admin-competition-close" type="button" aria-label="Close form" onClick={() => setFormOpen(false)} disabled={saving}>×</button></div><form onSubmit={(event) => void handleSave(event)}>
        {formError && <div className="admin-competition-error-banner" role="alert">{formError}</div>}
        <label className="admin-competition-field">Name<input autoFocus type="text" value={form.name} maxLength={120} onChange={(event) => setForm((value) => ({ ...value, name: event.target.value }))} required /></label>
        <label className="admin-competition-field">Type<select value={form.type} onChange={(event) => changeType(event.target.value as Competition['type'])}><option value="SKI_SLALOM">Ski Slalom</option><option value="BIATHLON">Biathlon</option></select></label>
        <label className="admin-competition-field">Gender<select value={form.gender} onChange={(event) => setForm((value) => ({ ...value, gender: event.target.value as Competition['gender'] }))}><option value="MALE">Male</option><option value="FEMALE">Female</option></select></label>
        <label className="admin-competition-field">Minimum age<input type="number" min="0" step="1" value={form.minimumAge} onChange={(event) => setForm((value) => ({ ...value, minimumAge: event.target.value }))} required /></label>
        {form.type === 'BIATHLON' && <div className="admin-biathlon-form-fields"><div className="admin-biathlon-form-label"><span aria-hidden="true">◎</span><div><strong>Biathlon format</strong><small>Set the course length and shooting lap.</small></div></div><label className="admin-competition-field">Number of laps<input type="number" min="1" step="1" value={form.numberOfLaps} onChange={(event) => setForm((value) => ({ ...value, numberOfLaps: event.target.value }))} required /></label><label className="admin-competition-field">Shooting after laps<input type="number" min="1" max={form.numberOfLaps || undefined} step="1" value={form.shootingAfterLaps} onChange={(event) => setForm((value) => ({ ...value, shootingAfterLaps: event.target.value }))} required /><small>Must not exceed the number of laps.</small></label></div>}
        <div className="admin-competition-form-actions"><button className="admin-competition-secondary" type="button" onClick={() => setFormOpen(false)} disabled={saving}>Cancel</button><button className="admin-competition-primary" type="submit" disabled={saving}>{saving ? 'Saving...' : editing ? 'Save Changes' : 'Create Competition'}</button></div>
      </form></section></div>}

      {deleteTarget && <div className="admin-competition-modal-backdrop"><section className="admin-competition-modal admin-competition-confirm" role="alertdialog" aria-modal="true" aria-labelledby="admin-competition-delete-title" aria-describedby="admin-competition-delete-description"><span className="admin-competition-warning" aria-hidden="true">!</span><h2 id="admin-competition-delete-title">Delete {deleteTarget.name}?</h2><p id="admin-competition-delete-description">This competition will be permanently deleted. Any backend constraints related to existing results or registrations will be shown if deletion cannot proceed.</p>{deleteError && <div className="admin-competition-error-banner" role="alert">{deleteError}</div>}<div className="admin-competition-form-actions"><button className="admin-competition-secondary" type="button" onClick={() => setDeleteTarget(null)} disabled={deletingId !== null}>Cancel</button><button className="admin-competition-danger" type="button" onClick={() => void handleDelete()} disabled={deletingId !== null}>{deletingId === deleteTarget.id ? 'Deleting...' : 'Delete Competition'}</button></div></section></div>}
    </div>
  );
};
