import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '@context/AuthContext';
import '@styles/Navbar.css';

export const Navbar: React.FC = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isAccountMenuOpen, setIsAccountMenuOpen] = useState(false);
  const location = useLocation();
  const { session, signOut } = useAuth();

  const isActive = (path: string) => location.pathname === path || (path === '/competitions' && location.pathname.startsWith('/competitions/'));

  const handleNavClick = () => {
    setIsMenuOpen(false);
    setIsAccountMenuOpen(false);
  };

  return (
    <nav className="navbar">
      <div className="navbar-container container">
        <Link to="/" className="navbar-brand">
          <span className="navbar-brand-icon">❄</span>
          WINTER OLYMPICS
        </Link>

        <button
          className={`navbar-toggle ${isMenuOpen ? 'active' : ''}`}
          onClick={() => setIsMenuOpen(!isMenuOpen)}
          aria-label="Toggle navigation"
        >
          <span></span>
          <span></span>
          <span></span>
        </button>

        <ul className={`navbar-menu ${isMenuOpen ? 'active' : ''}`}>
          <li>
            <Link
              to="/"
              className={`navbar-link ${isActive('/') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Home
            </Link>
          </li>
          <li>
            <Link
              to="/competitions"
              className={`navbar-link ${isActive('/competitions') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Competitions
            </Link>
          </li>
          <li>
            <Link
              to="/rankings"
              className={`navbar-link ${isActive('/rankings') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Rankings
            </Link>
          </li>
          <li>
            <Link
              to="/medals"
              className={`navbar-link ${isActive('/medals') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Medals
            </Link>
          </li>
          <li>
            <Link
              to="/statistics"
              className={`navbar-link ${isActive('/statistics') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Statistics
            </Link>
          </li>
          <li className="navbar-divider"></li>
          {session ? (
            <li className="navbar-account-item">
              <button
                type="button"
                className={`navbar-account-toggle ${isAccountMenuOpen ? 'open' : ''}`}
                aria-expanded={isAccountMenuOpen}
                aria-controls="navbar-account-menu"
                onClick={() => setIsAccountMenuOpen((open) => !open)}
              >
                <span className="navbar-account-avatar" aria-hidden="true">{session.role === 'ADMIN' ? '✦' : '❄'}</span>
                <span className="navbar-account-copy">
                  <strong>{session.username}</strong>
                  <small>{session.role === 'ADMIN' ? 'ADMIN' : 'ATHLETE'}</small>
                </span>
                <span className="navbar-account-chevron" aria-hidden="true">⌄</span>
              </button>
              {isAccountMenuOpen && (
                <ul className="navbar-account-menu" id="navbar-account-menu">
                  {session.role === 'ATHLETE' ? (
                    <>
                      <li><Link to="/athlete" className={`navbar-link ${isActive('/athlete') ? 'active' : ''}`} onClick={handleNavClick}>Dashboard</Link></li>
                      <li><Link to="/athlete/profile" className={`navbar-link ${isActive('/athlete/profile') ? 'active' : ''}`} onClick={handleNavClick}>My Profile</Link></li>
                      <li><Link to="/athlete/competitions" className={`navbar-link ${isActive('/athlete/competitions') ? 'active' : ''}`} onClick={handleNavClick}>My Competitions</Link></li>
                    </>
                  ) : (
                    <li><Link to="/admin" className={`navbar-link ${isActive('/admin') ? 'active' : ''}`} onClick={handleNavClick}>Admin Dashboard</Link></li>
                  )}
                  <li className="navbar-account-menu-divider" />
                  <li>
                    <button
                      type="button"
                      className="navbar-link navbar-logout"
                      onClick={() => { signOut(); handleNavClick(); }}
                    >
                      Logout
                    </button>
                  </li>
                </ul>
              )}
            </li>
          ) : (
            <li>
              <Link
                to="/login"
                className={`navbar-link ${isActive('/login') ? 'active' : ''}`}
                onClick={handleNavClick}
              >
                Login
              </Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
};
