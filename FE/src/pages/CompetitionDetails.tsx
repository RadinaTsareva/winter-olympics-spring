import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { CompetitionNotFoundError, getCompetition } from '@services/competitionApi';
import type { Competition } from '../types';
import '@styles/content-page.css';
import '@styles/competitions.css';

export const CompetitionDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const competitionId = Number(id);
  const [competition, setCompetition] = useState<Competition | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notFound, setNotFound] = useState(false);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let isCurrent = true;

    const loadCompetition = async () => {
      setLoading(true);
      setError(null);
      setNotFound(false);

      if (!id || !Number.isInteger(competitionId) || competitionId <= 0) {
        setNotFound(true);
        setLoading(false);
        return;
      }

      try {
        const result = await getCompetition(competitionId);
        if (isCurrent) setCompetition(result);
      } catch (loadError) {
        if (!isCurrent) return;
        if (loadError instanceof CompetitionNotFoundError) {
          setNotFound(true);
        } else {
          setError('We could not load this competition. Check your connection and try again.');
        }
      } finally {
        if (isCurrent) setLoading(false);
      }
    };

    void loadCompetition();
    return () => { isCurrent = false; };
  }, [competitionId, id, retryCount]);

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">COMPETITIONS</div>
        <h1>{competition && !loading ? competition.name : 'Competition Details'}</h1>
        <p>Competition information and event requirements.</p>
      </div>

      <div className="competition-details-wrap">
        <Link className="competition-back-link" to="/competitions">← Back to Competitions</Link>

        {loading && (
          <div className="competitions-state" role="status" aria-live="polite">
            <span className="competition-spinner" aria-hidden="true" />
            <p>Loading competition...</p>
          </div>
        )}

        {!loading && notFound && (
          <div className="competitions-state" role="status">
            <h2>Competition not found</h2>
            <p>This competition may have been removed or the link may be incorrect.</p>
          </div>
        )}

        {!loading && error && (
          <div className="competitions-state competitions-error" role="alert">
            <h2>Unable to load competition</h2>
            <p>{error}</p>
            <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>
              Retry
            </button>
          </div>
        )}

        {!loading && !error && !notFound && competition && (
          <article className="competition-detail-card">
            <div className="competition-card-topline">
              <span className="competition-type">
                {competition.type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom'}
              </span>
              <span className="competition-detail-mark" aria-hidden="true">✦</span>
            </div>

            <dl className="competition-detail-grid">
              <div className="competition-detail-item competition-detail-title">
                <dt>Competition</dt>
                <dd>{competition.name}</dd>
              </div>
              <div className="competition-detail-item">
                <dt>Type</dt>
                <dd>{competition.type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom'}</dd>
              </div>
              <div className="competition-detail-item">
                <dt>Gender</dt>
                <dd>{competition.gender === 'MALE' ? "Men's" : "Women's"}</dd>
              </div>
              <div className="competition-detail-item">
                <dt>Minimum age</dt>
                <dd>{competition.minimumAge} years</dd>
              </div>
              {competition.type === 'BIATHLON' && (
                <>
                  <div className="competition-detail-item">
                    <dt>Number of laps</dt>
                    <dd>{competition.numberOfLaps ?? '—'}</dd>
                  </div>
                  <div className="competition-detail-item">
                    <dt>Shooting after lap</dt>
                    <dd>{competition.shootingAfterLaps ?? '—'}</dd>
                  </div>
                </>
              )}
            </dl>
          </article>
        )}
      </div>
    </div>
  );
};
