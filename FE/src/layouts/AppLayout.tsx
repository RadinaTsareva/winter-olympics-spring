import { ReactNode } from 'react';
import { Navbar } from '@components/Navbar';
import { Footer } from '@components/Footer';
import '@styles/AppLayout.css';

interface AppLayoutProps {
  children: ReactNode;
}

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
  return (
    <div className="app-layout">
      <Navbar />
      <main className="main-content">{children}</main>
      <Footer />
    </div>
  );
};


