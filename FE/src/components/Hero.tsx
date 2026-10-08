import { ReactNode } from 'react';
import '@styles/Hero.css';

interface HeroProps {
  label?: string;
  heading: string;
  description?: string;
  children?: ReactNode;
  showVisual?: boolean;
}

export const Hero: React.FC<HeroProps> = ({
  label,
  heading,
  description,
  children,
  showVisual = false,
}) => {
  return (
    <section className="hero">
      <div className="hero-background">
        <div className="hero-gradient"></div>
        {showVisual && (
          <div className="hero-visual">
            <div className="mountain mountain-1"></div>
            <div className="mountain mountain-2"></div>
            <div className="mountain mountain-3"></div>
            <div className="snow-layer"></div>
            <div className="ice-glow"></div>
          </div>
        )}
        <div className="hero-shape hero-shape-1"></div>
        <div className="hero-shape hero-shape-2"></div>
      </div>
      <div className="container hero-content">
        <div className="hero-text">
          {label && <div className="section-label">{label}</div>}
          <h1 className="hero-heading">{heading}</h1>
          {description && <p className="hero-description">{description}</p>}
          {children && <div className="hero-actions">{children}</div>}
        </div>
      </div>
    </section>
  );
};

