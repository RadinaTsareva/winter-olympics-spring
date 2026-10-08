import { useEffect, useState } from 'react';
import {
  getBiathlonMedals,
  getCompetitions,
  getCountryMedals,
  getSlalomMedals,
} from '@services/competitionApi';
import type { Competition, CompetitionMedal, CountryMedalStanding } from '../types';
import '@styles/content-page.css';
import '@styles/medals.css';

const medalDetails = [
  { key: 'GOLD', position: 1, title: 'Gold', icon: '🥇' },
  { key: 'SILVER', position: 2, title: 'Silver', icon: '🥈' },
  { key: 'BRONZE', position: 3, title: 'Bronze', icon: '🥉' },
] as const;

export const Medals: React.FC = () => {
  const [standings, setStandings] = useState<CountryMedalStanding[]>([]);
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [medalsByCompetition, setMedalsByCompetition] = useState<Record<number, CompetitionMedal[]>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let isCurrent = true;

    const loadMedals = async () => {
      setLoading(true);
      setError(false);

      try {
        const [countryStandings, availableCompetitions] = await Promise.all([
          getCountryMedals(),
          getCompetitions(),
        ]);
        if (!isCurrent) return;
        setStandings(countryStandings);
        setCompetitions(availableCompetitions);

        const competitionResults = await Promise.all(availableCompetitions.map(async (competition) => {
          const medals = competition.type === 'SKI_SLALOM'
            ? await getSlalomMedals(competition.id)
            : await getBiathlonMedals(competition.id);
          return [competition.id, medals] as const;
        }));

        if (isCurrent) setMedalsByCompetition(Object.fromEntries(competitionResults));
      } catch {
        if (isCurrent) setError(true);
      } finally {
        if (isCurrent) setLoading(false);
      }
    };

    void loadMedals();
    return () => { isCurrent = false; };
  }, [retryCount]);

  const medalFor = (medals: CompetitionMedal[], key: typeof medalDetails[number]['key'], position: number) =>
    medals.find((medal) => medal.medal === key) ?? medals.find((medal) => medal.position === position);

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">ACHIEVEMENTS</div>
        <h1>Medals</h1>
        <p>Track gold, silver, and bronze medals across all competitions.</p>
      </div>

      {loading && (
        <div className="medals-state" role="status" aria-live="polite">
          <span className="competition-spinner" aria-hidden="true" />
          <p>Loading medal standings...</p>
        </div>
      )}

      {!loading && error && (
        <div className="medals-state medals-error" role="alert">
          <h2>Could not load medal standings</h2>
          <p>Check your connection and try again.</p>
          <button className="competition-retry" type="button" onClick={() => setRetryCount((count) => count + 1)}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && (
        <div className="medals-content">
          <section className="medals-section" aria-labelledby="overall-medals-title">
            <div className="medals-section-heading">
              <div>
                <div className="medals-eyebrow">NATIONS</div>
                <h2 id="overall-medals-title">Overall medal standings</h2>
              </div>
              {standings.length > 0 && <span className="medals-country-count">{standings.length} countries</span>}
            </div>

            {standings.length === 0 ? (
              <div className="medals-empty"><h3>No medals awarded yet</h3><p>Country standings will appear when results are recorded.</p></div>
            ) : (
              <div className="medals-table-scroll">
                <table className="medals-table">
                  <thead><tr><th>Rank</th><th>Country</th><th><span aria-label="Gold">🥇</span> Gold</th><th><span aria-label="Silver">🥈</span> Silver</th><th><span aria-label="Bronze">🥉</span> Bronze</th><th>Total</th></tr></thead>
                  <tbody>{standings.map((standing, index) => (
                    <tr className={index === 0 ? 'medals-leader-row' : ''} key={standing.country}>
                      <td><span className={`medals-rank ${index === 0 ? 'medals-rank-leader' : ''}`}>{index === 0 ? '👑' : index + 1}</span></td>
                      <td className="medals-country">{standing.country}</td>
                      <td className="medals-count medals-gold">{standing.gold}</td>
                      <td className="medals-count medals-silver">{standing.silver}</td>
                      <td className="medals-count medals-bronze">{standing.bronze}</td>
                      <td className="medals-total">{standing.total}</td>
                    </tr>
                  ))}</tbody>
                </table>
              </div>
            )}
          </section>

          <section className="medals-section" aria-labelledby="competition-medals-title">
            <div className="medals-section-heading">
              <div>
                <div className="medals-eyebrow">PODIUMS</div>
                <h2 id="competition-medals-title">Medals by competition</h2>
              </div>
            </div>

            {competitions.length === 0 ? (
              <div className="medals-empty"><h3>No competitions available</h3><p>Competition podiums will appear here once competitions are listed.</p></div>
            ) : (
              <div className="competition-medal-grid">
                {competitions.map((competition) => {
                  const medals = medalsByCompetition[competition.id] ?? [];
                  return (
                    <article className="competition-medal-card" key={competition.id}>
                      <div className="competition-medal-card-heading">
                        <span className="competition-type">{competition.type === 'BIATHLON' ? 'Biathlon' : 'Ski Slalom'}</span>
                        <span className="competition-medal-gender">{competition.gender === 'MALE' ? "Men's" : "Women's"}</span>
                      </div>
                      <h3>{competition.name}</h3>
                      <div className="competition-podium">
                        {medalDetails.map((detail) => {
                          const winner = medalFor(medals, detail.key, detail.position);
                          return (
                            <div className={`podium-place podium-${detail.key.toLowerCase()}`} key={detail.key}>
                              <span className="podium-medal" aria-hidden="true">{detail.icon}</span>
                              <span className="podium-label">{detail.position === 1 ? '1st place' : detail.position === 2 ? '2nd place' : '3rd place'} · {detail.title}</span>
                              {winner ? (
                                <>
                                  <strong>{winner.athleteName}</strong>
                                  <span className="podium-country">{winner.country}</span>
                                </>
                              ) : (
                                <span className="podium-unawarded">Not awarded</span>
                              )}
                            </div>
                          );
                        })}
                      </div>
                      {medals.length === 0 && <p className="competition-no-medals">No results recorded for this competition yet.</p>}
                    </article>
                  );
                })}
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
};
