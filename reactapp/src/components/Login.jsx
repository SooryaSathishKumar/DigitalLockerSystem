import React, { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

export default function Login() {
  const { signIn, loading } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const adminLogin = searchParams.get('admin') === 'true';
  const [form, setForm] = useState({ email: '', password: '', role: adminLogin ? 'ADMIN' : 'USER' });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState('');

  const validate = () => {
    const next = {};
    if (!form.email.trim()) next.email = 'Email is required.';
    else if (!/^\S+@\S+\.\S+$/.test(form.email)) next.email = 'Enter a valid email address.';
    if (!form.password) next.password = 'Password is required.';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    if (!validate()) return;
    try {
      const signedInUser = await signIn(form.email, form.password, form.role);
      navigate(signedInUser.role === 'ADMIN' ? '/admin' : '/dashboard');
    } catch (err) {
      setFormError(err.message);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-panel">
        <p className="auth-mark mono">DIGITAL LOCKER</p>
        <h1>{adminLogin ? 'Administrator sign in' : 'Sign in to your locker'}</h1>
        <p className="auth-sub">
          {adminLogin ? 'Use your administrator credentials to manage the locker.' : 'Your documents, kept private and in order.'}
        </p>

        {formError && <div className="banner-error">{formError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              autoComplete="email"
            />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="field">
            <label htmlFor="role">Account type</label>
            <select
              id="role"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              autoComplete="current-password"
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <button type="submit" className="btn btn-primary auth-submit" disabled={loading}>
            {loading ? 'Signing in…' : adminLogin ? 'Sign in as admin' : 'Log in'}
          </button>
        </form>

        <p className="auth-switch">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
        {!adminLogin && (
          <p className="auth-switch">
            Administrator? <Link to="/login?admin=true">Sign in to admin console</Link>
          </p>
        )}
      </div>
    </div>
  );
}