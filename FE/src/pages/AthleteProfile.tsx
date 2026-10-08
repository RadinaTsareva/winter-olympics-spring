import { useEffect, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context/AuthContext';
import { ApiError } from '@services/apiClient';
import { deleteAthleteProfile, getCountries, getMyAthleteProfile, updateAthleteProfile } from '@services/athleteApi';
import type { AthleteProfileData, CountryOption } from '../types';
import '@styles/content-page.css';
import '@styles/athlete-profile.css';

type ProfileForm = {
  name: string;
  countryId: string;
  gender: '' | 'MALE' | 'FEMALE';
  dateOfBirth: string;
};

type ProfileErrors = Partial<Record<keyof ProfileForm, string>>;

const emptyForm: ProfileForm = { name: '', countryId: '', gender: '', dateOfBirth: '' };

const toForm = (profile: AthleteProfileData): ProfileForm => ({
  name: profile.name,
  countryId: String(profile.countryId),
  gender: profile.gender,
  dateOfBirth: profile.dateOfBirth,
});

const displayDate = (date: string) => {
  const parsed = new Date(`${date}T00:00:00`);
  return Number.isNaN(parsed.getTime()) ? date : new Intl.DateTimeFormat(undefined, { dateStyle: 'long' }).format(parsed);
};

export const AthleteProfile: React.FC = () => {
  const navigate = useNavigate();
  const { signOut } = useAuth();
  const [profile, setProfile] = useState<AthleteProfileData | null>(null);
  const [profileLoading, setProfileLoading] = useState(true);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [retryCount, setRetryCount] = useState(0);
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState<ProfileForm>(emptyForm);
  const [errors, setErrors] = useState<ProfileErrors>({});
  const [countries, setCountries] = useState<CountryOption[]>([]);
  const [countriesLoading, setCountriesLoading] = useState(false);
  const [countriesError, setCountriesError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  useEffect(() => {
    let isCurrent = true;

    const loadProfile = async () => {
      setProfileLoading(true);
      setProfileError(null);
      try {
        const currentAthlete = await getMyAthleteProfile();
        if (isCurrent) setProfile(currentAthlete);
      } catch (error) {
        if (!isCurrent) return;
        if (error instanceof ApiError && error.status === 403) {
          setProfileError('Your session is not authorized to view an athlete profile. Sign in with the athlete account linked to this profile.');
        } else if (error instanceof ApiError && error.status === 401) {
          setProfileError('Your session has expired. Please sign in again to view your profile.');
        } else {
          setProfileError(error instanceof Error ? error.message : 'Could not load your athlete profile.');
        }
      } finally {
        if (isCurrent) setProfileLoading(false);
      }
    };

    void loadProfile();
    return () => { isCurrent = false; };
  }, [retryCount]);

  const loadCountries = async () => {
    setCountriesLoading(true);
    setCountriesError(null);
    try {
      setCountries(await getCountries());
    } catch (error) {
      setCountriesError(error instanceof Error ? error.message : 'Could not load countries.');
    } finally {
      setCountriesLoading(false);
    }
  };

  const beginEditing = () => {
    if (!profile) return;
    setForm(toForm(profile));
    setErrors({});
    setSaveError(null);
    setSuccessMessage(null);
    setEditing(true);
    if (countries.length === 0) void loadCountries();
  };

  const cancelEditing = () => {
    setEditing(false);
    setErrors({});
    setSaveError(null);
    if (profile) setForm(toForm(profile));
  };

  const validateForm = (): ProfileErrors => {
    const nextErrors: ProfileErrors = {};
    if (!form.name.trim()) nextErrors.name = 'Name is required.';
    if (!form.countryId || !countries.some((country) => String(country.id) === form.countryId)) {
      nextErrors.countryId = 'Select a country.';
    }
    if (form.gender !== 'MALE' && form.gender !== 'FEMALE') nextErrors.gender = 'Select a gender.';
    if (!form.dateOfBirth) nextErrors.dateOfBirth = 'Date of birth is required.';
    else if (form.dateOfBirth > new Date().toISOString().slice(0, 10)) nextErrors.dateOfBirth = 'Date of birth cannot be in the future.';
    return nextErrors;
  };

  const handleSave = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!profile) return;

    const validationErrors = validateForm();
    setErrors(validationErrors);
    setSaveError(null);
    setSuccessMessage(null);
    if (Object.keys(validationErrors).length > 0) return;

    setSaving(true);
    try {
      await updateAthleteProfile(profile.id, {
        name: form.name.trim(),
        countryId: Number(form.countryId),
        gender: form.gender as 'MALE' | 'FEMALE',
        dateOfBirth: form.dateOfBirth,
      });
      const selectedCountry = countries.find((country) => country.id === Number(form.countryId));
      setProfile({
        ...profile,
        name: form.name.trim(),
        countryId: Number(form.countryId),
        country: selectedCountry?.name ?? profile.country,
        gender: form.gender as 'MALE' | 'FEMALE',
        dateOfBirth: form.dateOfBirth,
      });
      setEditing(false);
      setSuccessMessage('Your profile was updated successfully.');
    } catch (error) {
      setSaveError(error instanceof Error ? error.message : 'Could not save your profile. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!profile) return;
    setDeleting(true);
    setDeleteError(null);
    try {
      await deleteAthleteProfile(profile.id);
      signOut();
      navigate('/', { replace: true });
    } catch (error) {
      setDeleteError(error instanceof Error ? error.message : 'Could not delete your profile. Please try again.');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">ATHLETE ACCOUNT</div>
        <h1>My Profile</h1>
        <p>View and manage your athlete information.</p>
      </div>

      {profileLoading && (
        <div className="athlete-profile-state" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading your athlete profile...</p>
        </div>
      )}

      {!profileLoading && profileError && (
        <div className="athlete-profile-state athlete-profile-error" role="alert">
          <h2>Profile unavailable</h2>
          <p>{profileError}</p>
          <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>Retry</button>
        </div>
      )}

      {!profileLoading && !profileError && profile && (
        <section className="athlete-profile-card" aria-label="My athlete profile">
          <div className="athlete-profile-card-header">
            <div className="athlete-profile-identity">
              <span className="athlete-profile-emblem" aria-hidden="true">❄</span>
              <div>
                <div className="profile-eyebrow">ATHLETE PROFILE</div>
                <h2>{profile.name}</h2>
              </div>
            </div>
            {!editing && (
              <button className="profile-edit-button" type="button" onClick={beginEditing}>Edit profile</button>
            )}
          </div>

          {successMessage && <div className="profile-success" role="status">{successMessage}</div>}

          {editing ? (
            <form className="profile-form" onSubmit={handleSave} noValidate>
              <label className="profile-field">
                <span>Name</span>
                <input
                  type="text"
                  value={form.name}
                  onChange={(event) => setForm({ ...form, name: event.target.value })}
                  aria-invalid={Boolean(errors.name)}
                  aria-describedby={errors.name ? 'profile-name-error' : undefined}
                  maxLength={120}
                  disabled={saving}
                />
                {errors.name && <small id="profile-name-error" className="profile-field-error">{errors.name}</small>}
              </label>

              <label className="profile-field">
                <span>Country</span>
                <select
                  value={form.countryId}
                  onChange={(event) => setForm({ ...form, countryId: event.target.value })}
                  aria-invalid={Boolean(errors.countryId)}
                  aria-describedby={errors.countryId ? 'profile-country-error' : undefined}
                  disabled={saving || countriesLoading || Boolean(countriesError)}
                >
                  <option value="">{countriesLoading ? 'Loading countries...' : 'Select a country'}</option>
                  {countries.map((country) => <option key={country.id} value={country.id}>{country.name}</option>)}
                </select>
                {errors.countryId && <small id="profile-country-error" className="profile-field-error">{errors.countryId}</small>}
                {countriesError && (
                  <span className="profile-country-error" role="alert">
                    {countriesError}{' '}
                    <button type="button" onClick={() => void loadCountries()} disabled={countriesLoading}>Retry countries</button>
                  </span>
                )}
              </label>

              <label className="profile-field">
                <span>Gender</span>
                <select
                  value={form.gender}
                  onChange={(event) => setForm({ ...form, gender: event.target.value as ProfileForm['gender'] })}
                  aria-invalid={Boolean(errors.gender)}
                  aria-describedby={errors.gender ? 'profile-gender-error' : undefined}
                  disabled={saving}
                >
                  <option value="">Select gender</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                </select>
                {errors.gender && <small id="profile-gender-error" className="profile-field-error">{errors.gender}</small>}
              </label>

              <label className="profile-field">
                <span>Date of birth</span>
                <input
                  type="date"
                  value={form.dateOfBirth}
                  onChange={(event) => setForm({ ...form, dateOfBirth: event.target.value })}
                  max={new Date().toISOString().slice(0, 10)}
                  aria-invalid={Boolean(errors.dateOfBirth)}
                  aria-describedby={errors.dateOfBirth ? 'profile-date-error' : undefined}
                  disabled={saving}
                />
                {errors.dateOfBirth && <small id="profile-date-error" className="profile-field-error">{errors.dateOfBirth}</small>}
              </label>

              {saveError && <div className="profile-form-error profile-form-wide" role="alert">{saveError}</div>}
              <div className="profile-form-actions profile-form-wide">
                <button className="profile-save-button" type="submit" disabled={saving || countriesLoading}>
                  {saving ? <><span className="auth-button-spinner" aria-hidden="true" /> Saving...</> : 'Save changes'}
                </button>
                <button className="profile-cancel-button" type="button" onClick={cancelEditing} disabled={saving}>Cancel</button>
              </div>
            </form>
          ) : (
            <dl className="profile-details-grid">
              <div className="profile-detail-item"><dt>Name</dt><dd>{profile.name}</dd></div>
              <div className="profile-detail-item"><dt>Country</dt><dd>{profile.country}</dd></div>
              <div className="profile-detail-item"><dt>Gender</dt><dd>{profile.gender === 'MALE' ? 'Male' : 'Female'}</dd></div>
              <div className="profile-detail-item"><dt>Date of birth</dt><dd>{displayDate(profile.dateOfBirth)}</dd></div>
            </dl>
          )}

          {!editing && (
            <div className="profile-danger-zone">
              <div><strong>Delete profile</strong><p>Permanently remove your athlete profile and its associated account link.</p></div>
              <button className="profile-delete-button" type="button" onClick={() => { setDeleteError(null); setConfirmDelete(true); }}>
                Delete profile
              </button>
            </div>
          )}
        </section>
      )}

      {confirmDelete && profile && (
        <div className="profile-dialog-backdrop">
          <section className="profile-delete-dialog" role="alertdialog" aria-modal="true" aria-labelledby="delete-dialog-title" aria-describedby="delete-dialog-description">
            <span className="profile-dialog-icon" aria-hidden="true">!</span>
            <h2 id="delete-dialog-title">Permanently delete your profile?</h2>
            <p id="delete-dialog-description">This action is permanent. Your athlete profile will be deleted and you will be signed out.</p>
            {deleteError && <div className="profile-form-error" role="alert">{deleteError}</div>}
            <div className="profile-dialog-actions">
              <button className="profile-cancel-button" type="button" onClick={() => setConfirmDelete(false)} disabled={deleting}>Cancel</button>
              <button className="profile-delete-button" type="button" onClick={() => void handleDelete()} disabled={deleting}>
                {deleting ? 'Deleting...' : 'Delete permanently'}
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
};
