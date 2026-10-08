import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@context/AuthContext';
import { ApiError } from '@services/apiClient';
import '@styles/content-page.css';
import '@styles/authentication.css';

export const Login: React.FC = () => {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const session = await signIn({ username: username.trim(), password });
      navigate(session.role === 'ADMIN' ? '/admin' : '/athlete', { replace: true });
    } catch (loginError) {
      setError(loginError instanceof ApiError || loginError instanceof Error
        ? loginError.message
        : 'Unable to sign in. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="content-page">
      <div className="page-header">
        <div className="section-label">ACCESS</div>
        <h1>Welcome back</h1>
        <p>Sign in to your Winter Olympics account.</p>
      </div>

      <section className="auth-card" aria-label="Sign in">
        <div className="auth-card-icon" aria-hidden="true">❄</div>
        <h2>Sign in</h2>
        <p className="auth-card-description">Enter your account details to continue.</p>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label className="auth-field">
            <span>Username</span>
            <input
              type="text"
              name="username"
              autoComplete="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              required
              disabled={submitting}
            />
          </label>

          <label className="auth-field">
            <span>Password</span>
            <input
              type="password"
              name="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              disabled={submitting}
            />
          </label>

          {error && <div className="auth-error" role="alert">{error}</div>}

          <button className="auth-submit" type="submit" disabled={submitting}>
            {submitting ? <><span className="auth-button-spinner" aria-hidden="true" /> Signing in...</> : 'Sign in'}
          </button>
        </form>

        <p className="auth-switch">New to the games? <Link to="/register">Create an account</Link></p>
      </section>
    </div>
  );
};
