import { useNavigate } from 'react-router-dom';
import { Hero } from '@components/Hero';
import { Button } from '@components/Button';
import { FeatureCard } from '@components/FeatureCard';
import { ExploreCard } from '@components/ExploreCard';
import { StatsPreview } from '@components/StatsPreview';
import '@styles/home.css';

export const Home: React.FC = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: '⛷️',
      title: 'SKI SLALOM',
      description: 'Precision and speed across two runs.',
    },
    {
      icon: '🎿',
      title: 'BIATHLON',
      description: 'Skiing endurance meets shooting accuracy.',
    },
    {
      icon: '🏅',
      title: 'MEDAL TABLE',
      description: 'Track gold, silver and bronze across the Games.',
    },
  ];

  return (
    <div className="home">
      <Hero
        label="WINTER OLYMPICS 2026"
        heading="Compete.
Perform.
Win."
        description="Follow winter sports competitions, athletes, rankings and medals in one place."
        showVisual={true}
      >
        <Button onClick={() => navigate('/competitions')} size="lg">
          VIEW COMPETITIONS
        </Button>
        <Button onClick={() => navigate('/rankings')} variant="secondary" size="lg">
          VIEW RANKINGS
        </Button>
      </Hero>

      <section className="features-section">
        <div className="container">
          <div className="section-header">
            <div className="section-label text-center">THE WINTER GAMES</div>
            <h2 className="section-title">Everything you need to follow the competition.</h2>
          </div>
          <div className="features-grid">
            {features.map((feature, index) => (
              <FeatureCard
                key={index}
                icon={feature.icon}
                title={feature.title}
                description={feature.description}
                onClick={() => navigate('/competitions')}
              />
            ))}
          </div>
        </div>
      </section>

      <section className="explore-section">
        <div className="container">
          <div className="section-header">
            <h2 className="section-title text-center">FOLLOW THE ACTION</h2>
          </div>
          <div className="explore-grid">
            <ExploreCard
              title="COMPETITIONS"
              onClick={() => navigate('/competitions')}
            >
              Browse all winter sports competitions
            </ExploreCard>
            <ExploreCard
              title="RANKINGS"
              onClick={() => navigate('/rankings')}
            >
              See where athletes stand
            </ExploreCard>
            <ExploreCard
              title="MEDALS"
              onClick={() => navigate('/medals')}
            >
              Track medal standings
            </ExploreCard>
          </div>
        </div>
      </section>

      <StatsPreview />
    </div>
  );
};

