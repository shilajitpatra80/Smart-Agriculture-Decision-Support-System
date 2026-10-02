import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Already logged in? Skip straight to the right dashboard.
  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/farmer'} replace />;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await api.post('/api/auth/login', { email, password });
      login(response);
      navigate(response.role === 'ADMIN' ? '/admin' : '/farmer');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <div className="hero mt-4" style={{ marginBottom: 'var(--space-4)' }}>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: '2rem', color: 'var(--color-accent-strong)' }}>🌾</div>
        <h1>Smart Agriculture</h1>
        <p className="muted">Sign in to check your crop recommendations, irrigation schedule, and more.</p>
      </div>

      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <label htmlFor="email">Email</label>
          <input id="email" type="email" required value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" />

          <label htmlFor="password">Password</label>
          <input id="password" type="password" required value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" />

          <button type="submit" className="btn-primary btn-block mt-4" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </div>

      <p className="muted" style={{ textAlign: 'center' }}>
        New here? <Link to="/register">Create a farmer account</Link>
      </p>
    </div>
  );
}
