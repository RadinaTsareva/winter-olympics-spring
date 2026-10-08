import { useEffect, useState } from 'react';
import { getOlympicStatistics } from '@services/competitionApi';
import type { OlympicStatistics } from '../types';
import '@styles/content-page.css';
import '@styles/statistics.css';

export const Statistics: React.FC = () => {
  const [statistics, setStatistics] = useState<OlympicStatistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let isCurrent = true;

    const loadStatistics = async () => {
      setLoading(true);
      setError(false);
      try {
        const result = await getOlympicStatistics();
        if (isCurrent) setStatistics(result);
      } catch {
        if (isCurrent) setError(true);
      } finally {
        if (isCurrent) setLoading(false);
      }
    };

    void loadStatistics();
    return () => { isCurrent = false; };
  }, [retryCount]);

  const hasAnyData = statistics !== null && (
    (statistics.averageParticipantAge != null && statistics.averageParticipantAge > 0) ||
    statistics.youngestMedalist != null ||
    statistics.oldestMedalist != null
  );

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">DATA & INSIGHTS</div>
        <h1>Statistics</h1>
        <p>A closer look at the athletes and achievements behind the games.</p>
      </div>

      {loading && (
        <div className="statistics-state" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading Olympic statistics...</p>
        </div>
      )}

      {!loading && error && (
        <div className="statistics-state statistics-error" role="alert">
          <h2>Could not load statistics</h2>
          <p>Check your connection and try again.</p>
          <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && !hasAnyData && (
        <div className="statistics-state">
          <span className="statistics-empty-icon" aria-hidden="true">❄</span>
          <h2>Statistics are not available yet</h2>
          <p>Age statistics will appear once participant and medal data are available.</p>
        </div>
      )}

      {!loading && !error && hasAnyData && statistics && (
        <section className="statistics-grid" aria-label="Olympic statistics">
          <article className="statistic-card statistic-average">
            <div className="statistic-card-top">
              <span className="statistic-icon" aria-hidden="true">❄</span>
              <span className="statistic-tag">ATHLETE PROFILE</span>
            </div>
            <p className="statistic-label">Average participant age</p>
            {statistics.averageParticipantAge == null || statistics.averageParticipantAge <= 0 ? (
              <p className="statistic-unavailable">Not available</p>
            ) : (
              <p className="statistic-value">{statistics.averageParticipantAge.toFixed(1)}<span> years</span></p>
            )}
            <div className="statistic-orbit" aria-hidden="true" />
          </article>

          <article className="statistic-card statistic-youngest">
            <div className="statistic-card-top">
              <span className="statistic-icon" aria-hidden="true">✧</span>
              <span className="statistic-tag">YOUNGEST MEDALIST</span>
            </div>
            {statistics.youngestMedalist ? (
              <>
                <h2 className="statistic-athlete-name">{statistics.youngestMedalist.athleteName}</h2>
                <p className="statistic-age">{statistics.youngestMedalist.age}<span> years old</span></p>
              </>
            ) : (
              <p className="statistic-unavailable">No youngest medalist data yet</p>
            )}
            <div className="statistic-snowline" aria-hidden="true" />
          </article>

          <article className="statistic-card statistic-oldest">
            <div className="statistic-card-top">
              <span className="statistic-icon" aria-hidden="true">✦</span>
              <span className="statistic-tag">OLDEST MEDALIST</span>
            </div>
            {statistics.oldestMedalist ? (
              <>
                <h2 className="statistic-athlete-name">{statistics.oldestMedalist.athleteName}</h2>
                <p className="statistic-age">{statistics.oldestMedalist.age}<span> years old</span></p>
              </>
            ) : (
              <p className="statistic-unavailable">No oldest medalist data yet</p>
            )}
            <div className="statistic-snowline" aria-hidden="true" />
          </article>
        </section>
      )}
    </div>
  );
};
