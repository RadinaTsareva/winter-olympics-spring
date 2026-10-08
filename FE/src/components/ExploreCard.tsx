import { ReactNode } from 'react';
import '@styles/ExploreCard.css';

interface ExploreCardProps {
  title: string;
  icon?: string;
  children?: ReactNode;
  onClick?: () => void;
}

export const ExploreCard: React.FC<ExploreCardProps> = ({
  title,
  icon,
  children,
  onClick,
}) => {
  return (
    <div className="explore-card" onClick={onClick}>
      {icon && <div className="explore-card-icon">{icon}</div>}
      <h3 className="explore-card-title">{title}</h3>
      {children && <div className="explore-card-content">{children}</div>}
    </div>
  );
};

