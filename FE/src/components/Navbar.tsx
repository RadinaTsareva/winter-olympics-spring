import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import '@styles/Navbar.css';

export const Navbar: React.FC = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  const handleNavClick = () => {
    setIsMenuOpen(false);
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
          <li>
            <Link
              to="/login"
              className={`navbar-link ${isActive('/login') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Login
            </Link>
          </li>
          <li>
            <Link
              to="/register"
              className={`navbar-link navbar-link-register ${isActive('/register') ? 'active' : ''}`}
              onClick={handleNavClick}
            >
              Register
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};


