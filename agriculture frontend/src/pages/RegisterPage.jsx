import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/farmer'} replace />;
  }

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await api.post('/api/auth/register', {
        name: form.name.trim(),
        email: form.email.trim(),
        phone: form.phone.trim() || null,
        password: form.password,
      });
      login(response); // new accounts are always FARMER — see AuthService.register
      navigate('/farmer');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <h1 style={{ marginBottom: 'var(--space-2)' }}>Create your account</h1>
      <p className="muted mt-4" style={{ marginBottom: 'var(--space-4)' }}>
        Every new account starts as a farmer account. Admin access is granted separately.
      </p>

      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <label htmlFor="name">Full name</label>
          <input id="name" required value={form.name} onChange={(e) => update('name', e.target.value)} placeholder="e.g. Ramesh Patil" />

          <label htmlFor="email">Email</label>
          <input id="email" type="email" required value={form.email} onChange={(e) => update('email', e.target.value)} placeholder="you@example.com" />

          <label htmlFor="phone">Phone (optional)</label>
          <input id="phone" type="tel" value={form.phone} onChange={(e) => update('phone', e.target.value)} placeholder="e.g. 9876543210" />

          <label htmlFor="password">Password</label>
          <input id="password" type="password" required minLength={6} value={form.password} onChange={(e) => update('password', e.target.value)} placeholder="At least 6 characters" />

          <button type="submit" className="btn-primary btn-block mt-4" disabled={loading}>
            {loading ? 'Creating account…' : 'Create account'}
          </button>
        </form>
      </div>

      <p className="muted" style={{ textAlign: 'center' }}>
        Already have an account? <Link to="/">Sign in</Link>
      </p>
    </div>
  );
}
