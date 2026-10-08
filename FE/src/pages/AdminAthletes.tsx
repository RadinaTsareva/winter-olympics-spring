import { useEffect, useState, type FormEvent } from 'react';
import { ApiError } from '@services/apiClient';
import { createAthlete, deleteAthleteProfile, getAthletes, getCountries, updateAthleteProfile } from '@services/athleteApi';
import type { AthleteProfileData, AthleteProfileUpdate, CountryOption } from '../types';
import '@styles/content-page.css';
import '@styles/admin-athletes.css';

const blankForm: AthleteProfileUpdate = { name: '', countryId: 0, gender: 'MALE', dateOfBirth: '' };

function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.status === 401 || error.status === 403) return 'Your admin session is not authorized for this action. Sign in with an administrator account and try again.';
    const message = error.message.toLowerCase();
    if (message.includes('foreign key constraint') && message.includes('competition_registrations')) {
      return 'This athlete still has competition registrations and cannot be deleted yet. Remove their registrations from Admin → Registrations, then try again.';
    }
    return error.message;
  }
  if (error instanceof Error && (error.message.toLowerCase().includes('fetch') || error.message.toLowerCase().includes('network'))) {
    return 'Could not connect to the server. Check your connection and try again.';
  }
  return 'Something went wrong. Please try again.';
}

export const AdminAthletes: React.FC = () => {
  const [athletes, setAthletes] = useState<AthleteProfileData[]>([]);
  const [countries, setCountries] = useState<CountryOption[]>([]);
  const [athletesLoading, setAthletesLoading] = useState(true);
  const [countriesLoading, setCountriesLoading] = useState(true);
  const [athletesError, setAthletesError] = useState<string | null>(null);
  const [countriesError, setCountriesError] = useState<string | null>(null);
  const [athleteRetry, setAthleteRetry] = useState(0);
  const [countryRetry, setCountryRetry] = useState(0);
  const [formOpen, setFormOpen] = useState(false);
  const [editingAthlete, setEditingAthlete] = useState<AthleteProfileData | null>(null);
  const [form, setForm] = useState<AthleteProfileUpdate>(blankForm);
  const [formError, setFormError] = useState<string | null>(null);
  const [pageError, setPageError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [athleteToDelete, setAthleteToDelete] = useState<AthleteProfileData | null>(null);

  useEffect(() => {
    let active = true;
    setAthletesLoading(true);
    setAthletesError(null);
    getAthletes().then((data) => {
      if (active) setAthletes(data);
    }).catch((error: unknown) => {
      if (active) setAthletesError(errorMessage(error));
    }).finally(() => { if (active) setAthletesLoading(false); });
    return () => { active = false; };
  }, [athleteRetry]);

  useEffect(() => {
    let active = true;
    setCountriesLoading(true);
    setCountriesError(null);
    getCountries().then((data) => {
      if (active) setCountries(data);
    }).catch((error: unknown) => {
      if (active) setCountriesError(errorMessage(error));
    }).finally(() => { if (active) setCountriesLoading(false); });
    return () => { active = false; };
  }, [countryRetry]);

  const openCreate = () => {
    setEditingAthlete(null);
    setForm({ ...blankForm, countryId: countries[0]?.id ?? 0 });
    setFormError(null);
    setPageError(null);
    setFormOpen(true);
  };

  const openEdit = (athlete: AthleteProfileData) => {
    setEditingAthlete(athlete);
    setForm({ name: athlete.name, countryId: athlete.countryId, gender: athlete.gender, dateOfBirth: athlete.dateOfBirth });
    setFormError(null);
    setPageError(null);
    setFormOpen(true);
  };

  const refreshAthletes = async () => {
    const updated = await getAthletes();
    setAthletes(updated);
    setAthletesError(null);
  };

  const handleSave = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setPageError(null);
    setSuccess(null);
    const cleanName = form.name.trim();
    if (!cleanName || !form.countryId || !form.dateOfBirth) {
      setFormError('Complete all fields before saving.');
      return;
    }
    setSaving(true);
    try {
      const payload = { ...form, name: cleanName };
      if (editingAthlete) {
        await updateAthleteProfile(editingAthlete.id, payload);
        setSuccess(`${cleanName} was updated successfully.`);
      } else {
        await createAthlete(payload);
        setSuccess(`${cleanName} was created successfully.`);
      }
      setFormOpen(false);
      try {
        await refreshAthletes();
      } catch (error) {
        setAthletesError(`The change succeeded, but the athlete list could not be refreshed. ${errorMessage(error)}`);
      }
    } catch (error) {
      setFormError(errorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!athleteToDelete || deletingId !== null) return;
    const target = athleteToDelete;
    setDeletingId(target.id);
    setPageError(null);
    setSuccess(null);
    try {
      await deleteAthleteProfile(target.id);
      setAthleteToDelete(null);
      setSuccess(`${target.name} was deleted successfully.`);
      try {
        await refreshAthletes();
      } catch (error) {
        setAthletesError(`The athlete was deleted, but the list could not be refreshed. ${errorMessage(error)}`);
      }
    } catch (error) {
      setPageError(errorMessage(error));
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (value: string) => {
    if (!value) return '—';
    const date = new Date(`${value}T00:00:00`);
    return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(date);
  };

  return (
    <div className="content-page admin-athletes-page">
      <div className="page-header">
        <div className="section-label">ADMIN · MANAGEMENT</div>
        <h1>Manage Athletes</h1>
        <p>Maintain athlete profiles participating in the Winter Olympics.</p>
      </div>

      <section className="admin-athletes-panel" aria-label="Athlete management">
        <div className="admin-athletes-toolbar">
          <div><span className="admin-athletes-kicker">ATHLETE DIRECTORY</span><h2>Athletes <span>{athletes.length}</span></h2></div>
          <button className="admin-athlete-primary" type="button" onClick={openCreate} disabled={countriesLoading || Boolean(countriesError) || countries.length === 0}>＋ Add Athlete</button>
        </div>

        {countriesError && <div className="admin-athletes-inline-error" role="alert"><span>Country list unavailable: {countriesError}</span><button type="button" onClick={() => setCountryRetry((value) => value + 1)}>Retry</button></div>}
        {countriesLoading && <p className="admin-athletes-note" role="status">Loading countries for athlete forms...</p>}
        {!countriesLoading && !countriesError && countries.length === 0 && <p className="admin-athletes-note" role="status">No countries are available. Athlete forms are disabled until countries can be loaded.</p>}
        {success && <div className="admin-athletes-success" role="status">{success}</div>}
        {pageError && <div className="admin-athletes-inline-error" role="alert"><span>{pageError}</span><button type="button" onClick={() => { setPageError(null); setAthleteRetry((value) => value + 1); }}>Retry list</button></div>}

        {athletesLoading && <div className="admin-athletes-state" role="status"><span className="admin-athlete-spinner" />Loading athletes...</div>}
        {!athletesLoading && athletesError && <div className="admin-athletes-state admin-athletes-error" role="alert"><h3>Could not load athletes</h3><p>{athletesError}</p><button type="button" onClick={() => setAthleteRetry((value) => value + 1)}>Retry</button></div>}
        {!athletesLoading && !athletesError && athletes.length === 0 && <div className="admin-athletes-state admin-athletes-empty"><span aria-hidden="true">♙</span><h3>No athletes yet</h3><p>Add an athlete to begin building the directory.</p></div>}

        {!athletesLoading && !athletesError && athletes.length > 0 && (
          <div className="admin-athletes-table-wrap">
            <table className="admin-athletes-table">
              <thead><tr><th scope="col">Name</th><th scope="col">Country</th><th scope="col">Gender</th><th scope="col">Date of birth</th><th scope="col">Actions</th></tr></thead>
              <tbody>{athletes.map((athlete) => <tr key={athlete.id}>
                <th scope="row"><span className="admin-athlete-avatar" aria-hidden="true">{athlete.name.trim().charAt(0).toUpperCase()}</span>{athlete.name}</th>
                <td>{athlete.country || countries.find((country) => country.id === athlete.countryId)?.name || '—'}</td>
                <td><span className="admin-gender-tag">{athlete.gender === 'MALE' ? 'Male' : 'Female'}</span></td>
                <td>{formatDate(athlete.dateOfBirth)}</td>
                <td><div className="admin-athlete-actions"><button type="button" onClick={() => openEdit(athlete)} disabled={countries.length === 0}>Edit</button><button className="admin-athlete-delete" type="button" onClick={() => { setPageError(null); setSuccess(null); setAthleteToDelete(athlete); }}>Delete</button></div></td>
              </tr>)}</tbody>
            </table>
          </div>
        )}
      </section>

      {formOpen && <div className="admin-athlete-modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget && !saving) setFormOpen(false); }}>
        <section className="admin-athlete-modal" role="dialog" aria-modal="true" aria-labelledby="admin-athlete-form-title">
          <div className="admin-athlete-modal-heading"><div><span className="admin-athletes-kicker">ATHLETE PROFILE</span><h2 id="admin-athlete-form-title">{editingAthlete ? 'Edit Athlete' : 'Add Athlete'}</h2></div><button className="admin-modal-close" type="button" aria-label="Close form" onClick={() => setFormOpen(false)} disabled={saving}>×</button></div>
          <form onSubmit={(event) => void handleSave(event)}>
            {formError && <div className="admin-athletes-inline-error" role="alert">{formError}</div>}
            <label className="admin-athlete-field">Name<input autoFocus type="text" autoComplete="name" value={form.name} maxLength={120} onChange={(event) => setForm((value) => ({ ...value, name: event.target.value }))} required /></label>
            <label className="admin-athlete-field">Country<select value={form.countryId || ''} onChange={(event) => setForm((value) => ({ ...value, countryId: Number(event.target.value) }))} required><option value="" disabled>Select a country</option>{countries.map((country) => <option key={country.id} value={country.id}>{country.name}</option>)}</select></label>
            <label className="admin-athlete-field">Gender<select value={form.gender} onChange={(event) => setForm((value) => ({ ...value, gender: event.target.value as AthleteProfileUpdate['gender'] }))}><option value="MALE">Male</option><option value="FEMALE">Female</option></select></label>
            <label className="admin-athlete-field">Date of birth<input type="date" value={form.dateOfBirth} onChange={(event) => setForm((value) => ({ ...value, dateOfBirth: event.target.value }))} required /></label>
            <div className="admin-athlete-form-actions"><button className="admin-athlete-secondary" type="button" onClick={() => setFormOpen(false)} disabled={saving}>Cancel</button><button className="admin-athlete-primary" type="submit" disabled={saving}>{saving ? 'Saving...' : editingAthlete ? 'Save Changes' : 'Create Athlete'}</button></div>
          </form>
        </section>
      </div>}

      {athleteToDelete && <div className="admin-athlete-modal-backdrop"><section className="admin-athlete-modal admin-athlete-confirm" role="alertdialog" aria-modal="true" aria-labelledby="admin-athlete-delete-title" aria-describedby="admin-athlete-delete-description"><span className="admin-athlete-warning" aria-hidden="true">!</span><h2 id="admin-athlete-delete-title">Delete {athleteToDelete.name}?</h2><p id="admin-athlete-delete-description">This will permanently delete this athlete profile. This action cannot be undone.</p>{pageError && <div className="admin-athletes-inline-error" role="alert">{pageError}</div>}<div className="admin-athlete-form-actions"><button className="admin-athlete-secondary" type="button" onClick={() => setAthleteToDelete(null)} disabled={deletingId !== null}>Cancel</button><button className="admin-athlete-danger" type="button" onClick={() => void handleDelete()} disabled={deletingId !== null}>{deletingId === athleteToDelete.id ? 'Deleting...' : 'Delete Athlete'}</button></div></section></div>}
    </div>
  );
};
