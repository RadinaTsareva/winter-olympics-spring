import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getCompetitions } from '@services/competitionApi';
import type { Competition } from '../types';
import '@styles/content-page.css';
import '@styles/competitions.css';

export const Competitions: React.FC = () => {
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let isCurrent = true;

    const loadCompetitions = async () => {
      setLoading(true);
      setError(null);

      try {
        const results = await getCompetitions();
        if (isCurrent) setCompetitions(results);
      } catch {
        if (isCurrent) setError('We could not load competitions. Check your connection and try again.');
      } finally {
        if (isCurrent) setLoading(false);
      }
    };

    void loadCompetitions();
    return () => { isCurrent = false; };
  }, [retryCount]);

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">WINTER OLYMPICS 2026</div>
        <h1>Competitions</h1>
        <p>Browse all competitions and find the ones you're interested in.</p>
      </div>

      {loading && (
        <div className="competitions-state" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading competitions...</p>
        </div>
      )}

      {!loading && error && (
        <div className="competitions-state competitions-error" role="alert">
          <h2>Unable to load competitions</h2>
          <p>{error}</p>
          <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && competitions.length === 0 && (
        <div className="competitions-state">
          <h2>No competitions yet</h2>
          <p>There are no competitions to display right now. Please check back soon.</p>
        </div>
      )}

      {!loading && !error && competitions.length > 0 && (
        <div className="competition-grid">
          {competitions.map((competition) => (
            <Link
              className="competition-card"
              key={competition.id}
              to={`/competitions/${competition.id}`}
              aria-label={`View ${competition.name} competition details`}
            >
              <div className="competition-card-topline">
                <span className="competition-type">
                  {competition.type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom'}
                </span>
                <span className="competition-arrow" aria-hidden="true">↗</span>
              </div>
              <h2>{competition.name}</h2>
              <div className="competition-meta">
                <span>{competition.gender === 'MALE' ? "Men's event" : "Women's event"}</span>
                <span>Minimum age {competition.minimumAge}</span>
              </div>
              {competition.type === 'BIATHLON' && (
                <div className="biathlon-details">
                  <div><span>Race distance</span><strong>{competition.numberOfLaps ?? '—'} laps</strong></div>
                  <div><span>Shooting stage</span><strong>After lap {competition.shootingAfterLaps ?? '—'}</strong></div>
                </div>
              )}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};
