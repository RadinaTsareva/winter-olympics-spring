import '@styles/StatsPreview.css';

interface StatItem {
  label: string;
  value?: string;
}

interface StatsPreviewProps {
  title?: string;
  stats?: StatItem[];
}

export const StatsPreview: React.FC<StatsPreviewProps> = ({
  title = 'THE GAMES AT A GLANCE',
  stats = [
    { label: 'COMPETITIONS', value: '—' },
    { label: 'ATHLETES', value: '—' },
    { label: 'MEDALS', value: '—' },
  ],
}) => {
  return (
    <section className="stats-preview">
      <div className="container">
        <h2 className="text-center stats-preview-title">{title}</h2>
        <div className="stats-grid">
          {stats.map((stat, index) => (
            <div key={index} className="stat-item">
              <div className="stat-label">{stat.label}</div>
              <div className="stat-value">{stat.value || '—'}</div>
            </div>
          ))}
        </div>
        <p className="stats-preview-note">
          Statistics will be updated as competitions progress.
        </p>
      </div>
    </section>
  );
};

