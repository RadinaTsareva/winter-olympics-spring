import { useEffect, useState } from 'react';
import { getBiathlonRanking, getCompetitions, getSlalomRanking } from '@services/competitionApi';
import type { BiathlonRankingEntry, Competition, SlalomRankingEntry } from '../types';
import '@styles/content-page.css';
import '@styles/rankings.css';

const formatTime = (time: number | null) => time == null ? '—' : `${time.toFixed(2)} s`;

const placeName = (position: number) => {
  if (position === 1) return '🥇';
  if (position === 2) return '🥈';
  if (position === 3) return '🥉';
  return String(position);
};

export const Rankings: React.FC = () => {
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [selectedId, setSelectedId] = useState('');
  const [competitionsLoading, setCompetitionsLoading] = useState(true);
  const [competitionsError, setCompetitionsError] = useState(false);
  const [competitionsRetry, setCompetitionsRetry] = useState(0);

  const [slalomRanking, setSlalomRanking] = useState<SlalomRankingEntry[]>([]);
  const [biathlonRanking, setBiathlonRanking] = useState<BiathlonRankingEntry[]>([]);
  const [rankingLoading, setRankingLoading] = useState(false);
  const [rankingError, setRankingError] = useState(false);
  const [rankingRetry, setRankingRetry] = useState(0);

  useEffect(() => {
    let isCurrent = true;

    const loadCompetitions = async () => {
      setCompetitionsLoading(true);
      setCompetitionsError(false);
      try {
        const results = await getCompetitions();
        if (!isCurrent) return;
        setCompetitions(results);
        setSelectedId((current) => results.some((item) => String(item.id) === current)
          ? current
          : results[0] ? String(results[0].id) : '');
      } catch {
        if (isCurrent) setCompetitionsError(true);
      } finally {
        if (isCurrent) setCompetitionsLoading(false);
      }
    };

    void loadCompetitions();
    return () => { isCurrent = false; };
  }, [competitionsRetry]);

  const selectedCompetition = competitions.find((competition) => String(competition.id) === selectedId);

  useEffect(() => {
    if (!selectedCompetition) {
      setSlalomRanking([]);
      setBiathlonRanking([]);
      setRankingLoading(false);
      setRankingError(false);
      return;
    }

    let isCurrent = true;
    const loadRanking = async () => {
      setRankingLoading(true);
      setRankingError(false);
      setSlalomRanking([]);
      setBiathlonRanking([]);

      try {
        if (selectedCompetition.type === 'SKI_SLALOM') {
          const results = await getSlalomRanking(selectedCompetition.id);
          if (isCurrent) setSlalomRanking(results);
        } else {
          const results = await getBiathlonRanking(selectedCompetition.id);
          if (isCurrent) setBiathlonRanking(results);
        }
      } catch {
        if (isCurrent) setRankingError(true);
      } finally {
        if (isCurrent) setRankingLoading(false);
      }
    };

    void loadRanking();
    return () => { isCurrent = false; };
  }, [selectedCompetition, rankingRetry]);

  const renderSlalomTable = () => (
    <div className="rank-table-scroll">
      <table className="rank-table">
        <thead><tr><th>Position</th><th>Athlete</th><th>Country</th><th>First run</th><th>Second run</th><th>Final time</th></tr></thead>
        <tbody>{slalomRanking.map((entry) => (
          <tr className={`rank-row rank-place-${entry.position}`} key={`${entry.position}-${entry.athleteName}`}>
            <td><span className="rank-position">{placeName(entry.position)}</span></td>
            <td className="rank-athlete">{entry.athleteName}</td>
            <td>{entry.country}</td>
            <td>{formatTime(entry.firstRunTime)}</td>
            <td>{formatTime(entry.secondRunTime)}</td>
            <td className="rank-final-time">{formatTime(entry.finalTime)}</td>
          </tr>
        ))}</tbody>
      </table>
    </div>
  );

  const renderBiathlonTable = () => (
    <div className="rank-table-scroll">
      <table className="rank-table">
        <thead><tr><th>Position</th><th>Athlete</th><th>Country</th><th>Ski time</th><th>Misses</th><th>Penalty time</th><th>Final time</th></tr></thead>
        <tbody>{biathlonRanking.map((entry) => (
          <tr className={`rank-row rank-place-${entry.position}`} key={`${entry.position}-${entry.athleteName}`}>
            <td><span className="rank-position">{placeName(entry.position)}</span></td>
            <td className="rank-athlete">{entry.athleteName}</td>
            <td>{entry.country}</td>
            <td>{formatTime(entry.skiTime)}</td>
            <td>{entry.misses}</td>
            <td>{formatTime(entry.penaltyTime)}</td>
            <td className="rank-final-time">{formatTime(entry.finalTime)}</td>
          </tr>
        ))}</tbody>
      </table>
    </div>
  );

  const hasRanking = selectedCompetition?.type === 'SKI_SLALOM'
    ? slalomRanking.length > 0
    : biathlonRanking.length > 0;

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">LEADERBOARD</div>
        <h1>Rankings</h1>
        <p>See where athletes stand in current competitions.</p>
      </div>

      <section className="rankings-panel" aria-label="Competition rankings">
        <div className="rankings-toolbar">
          <div>
            <div className="rankings-eyebrow">OFFICIAL RESULTS</div>
            <h2>Competition leaderboard</h2>
          </div>
          {!competitionsLoading && !competitionsError && competitions.length > 0 && (
            <label className="competition-select-wrap">
              <span>Select competition</span>
              <select value={selectedId} onChange={(event) => setSelectedId(event.target.value)}>
                {competitions.map((competition) => (
                  <option key={competition.id} value={competition.id}>{competition.name}</option>
                ))}
              </select>
            </label>
          )}
        </div>

        {competitionsLoading && (
          <div className="rankings-state" role="status"><span className="competition-spinner" aria-hidden="true" /><p>Loading competitions...</p></div>
        )}

        {!competitionsLoading && competitionsError && (
          <div className="rankings-state rankings-error" role="alert">
            <h3>Could not load competitions</h3>
            <p>Check your connection and try again.</p>
            <button className="competition-retry" type="button" onClick={() => setCompetitionsRetry((count) => count + 1)}>Retry</button>
          </div>
        )}

        {!competitionsLoading && !competitionsError && competitions.length === 0 && (
          <div className="rankings-state"><h3>No competitions available</h3><p>There are no competitions to rank yet.</p></div>
        )}

        {!competitionsLoading && !competitionsError && selectedCompetition && (
          <>
            <div className="ranking-caption">
              <span className="competition-type">{selectedCompetition.type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom'}</span>
              <span>{selectedCompetition.gender === 'MALE' ? "Men's event" : "Women's event"}</span>
            </div>

            {rankingLoading && (
              <div className="rankings-state" role="status"><span className="competition-spinner" aria-hidden="true" /><p>Loading rankings...</p></div>
            )}

            {!rankingLoading && rankingError && (
              <div className="rankings-state rankings-error" role="alert">
                <h3>Could not load rankings</h3>
                <p>Check your connection and try again.</p>
                <button className="competition-retry" type="button" onClick={() => setRankingRetry((count) => count + 1)}>Retry</button>
              </div>
            )}

            {!rankingLoading && !rankingError && !hasRanking && (
              <div className="rankings-state"><h3>No results yet</h3><p>This competition does not have any ranked results yet.</p></div>
            )}

            {!rankingLoading && !rankingError && hasRanking && (
              selectedCompetition.type === 'SKI_SLALOM' ? renderSlalomTable() : renderBiathlonTable()
            )}
          </>
        )}
      </section>
    </div>
  );
};
