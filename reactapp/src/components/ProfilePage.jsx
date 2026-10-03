import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getCurrentUser, updateProfile, changePassword } from '../api/client';

export default function ProfilePage() {
  const { user: authUser, signOut } = useAuth();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tab, setTab] = useState('profile'); // 'profile' | 'password'

  // Profile form
  const [profileForm, setProfileForm] = useState({ name: '', email: '' });
  const [profileSaving, setProfileSaving] = useState(false);
  const [profileMsg, setProfileMsg] = useState(null);

  // Password form
  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [pwErrors, setPwErrors] = useState({});
  const [pwSaving, setPwSaving] = useState(false);
  const [pwMsg, setPwMsg] = useState(null);

  useEffect(() => {
    getCurrentUser()
      .then((data) => {
        setUser(data);
        setProfileForm({ name: data.name || '', email: data.email || '' });
      })
      .catch(() => {
        // Fallback to auth context user
        const u = authUser || {};
        setUser(u);
        setProfileForm({ name: u.name || '', email: u.email || '' });
        setError('Could not load profile from server. Showing cached data.');
      })
      .finally(() => setLoading(false));
  }, [authUser]);

  const handleProfileSave = async (e) => {
    e.preventDefault();
    setProfileSaving(true);
    setProfileMsg(null);
    try {
      const updated = await updateProfile({ name: profileForm.name });
      setUser({ ...user, ...updated });
      setProfileMsg({ type: 'success', text: 'Profile updated successfully.' });
    } catch (err) {
      setProfileMsg({ type: 'error', text: err.friendlyMessage || 'Could not update profile.' });
    } finally {
      setProfileSaving(false);
    }
  };

  const validatePw = () => {
    const next = {};
    if (!pwForm.currentPassword) next.currentPassword = 'Current password is required.';
    if (!pwForm.newPassword) next.newPassword = 'New password is required.';
    else if (pwForm.newPassword.length < 8) next.newPassword = 'Password must be at least 8 characters.';
    if (!pwForm.confirmPassword) next.confirmPassword = 'Please confirm your new password.';
    else if (pwForm.newPassword !== pwForm.confirmPassword) next.confirmPassword = 'Passwords do not match.';
    setPwErrors(next);
    return Object.keys(next).length === 0;
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (!validatePw()) return;
    setPwSaving(true);
    setPwMsg(null);
    try {
      await changePassword({
        currentPassword: pwForm.currentPassword,
        newPassword: pwForm.newPassword,
      });
      setPwMsg({ type: 'success', text: 'Password changed successfully. Please log in again.' });
      setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setTimeout(signOut, 2000);
    } catch (err) {
      setPwMsg({ type: 'error', text: err.friendlyMessage || 'Could not change password.' });
    } finally {
      setPwSaving(false);
    }
  };

  if (loading) return <div className="loading-block">Loading profile…</div>;

  return (
    <div className="profile-page">
      <div className="page-header">
        <h1>My Profile</h1>
      </div>

      {error && <div className="banner-error" style={{ marginBottom: '1rem' }}>{error}</div>}

      <div className="profile-tabs">
        <button
          className={`profile-tab ${tab === 'profile' ? 'active' : ''}`}
          onClick={() => setTab('profile')}
        >
          Profile Details
        </button>
        <button
          className={`profile-tab ${tab === 'password' ? 'active' : ''}`}
          onClick={() => setTab('password')}
        >
          Change Password
        </button>
      </div>

      <div className="card profile-card">
        {tab === 'profile' ? (
          <form onSubmit={handleProfileSave} noValidate>
            <h2>Profile Details</h2>
            {profileMsg && <div className={`banner-${profileMsg.type}`}>{profileMsg.text}</div>}

            <div className="field">
              <label htmlFor="profile-name">Full Name</label>
              <input
                id="profile-name"
                type="text"
                value={profileForm.name}
                onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
              />
            </div>

            <div className="field">
              <label htmlFor="profile-email">Email</label>
              <input
                id="profile-email"
                type="email"
                value={profileForm.email}
                disabled
                style={{ background: 'var(--color-surface-muted)', cursor: 'not-allowed' }}
              />
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>
                Email cannot be changed.
              </span>
            </div>

            <div className="field">
              <label>Role</label>
              <input type="text" value={user?.role || authUser?.role || '—'} disabled
                style={{ background: 'var(--color-surface-muted)', cursor: 'not-allowed' }} />
            </div>

            <button type="submit" className="btn btn-primary" disabled={profileSaving}>
              {profileSaving ? 'Saving…' : 'Save Changes'}
            </button>
          </form>
        ) : (
          <form onSubmit={handlePasswordChange} noValidate>
            <h2>Change Password</h2>
            {pwMsg && <div className={`banner-${pwMsg.type}`}>{pwMsg.text}</div>}

            <div className="field">
              <label htmlFor="pw-current">Current Password</label>
              <input
                id="pw-current"
                type="password"
                value={pwForm.currentPassword}
                onChange={(e) => setPwForm({ ...pwForm, currentPassword: e.target.value })}
                autoComplete="current-password"
              />
              {pwErrors.currentPassword && <span className="field-error">{pwErrors.currentPassword}</span>}
            </div>

            <div className="field">
              <label htmlFor="pw-new">New Password</label>
              <input
                id="pw-new"
                type="password"
                value={pwForm.newPassword}
                onChange={(e) => setPwForm({ ...pwForm, newPassword: e.target.value })}
                autoComplete="new-password"
              />
              {pwErrors.newPassword && <span className="field-error">{pwErrors.newPassword}</span>}
            </div>

            <div className="field">
              <label htmlFor="pw-confirm">Confirm New Password</label>
              <input
                id="pw-confirm"
                type="password"
                value={pwForm.confirmPassword}
                onChange={(e) => setPwForm({ ...pwForm, confirmPassword: e.target.value })}
                autoComplete="new-password"
              />
              {pwErrors.confirmPassword && <span className="field-error">{pwErrors.confirmPassword}</span>}
            </div>

            <button type="submit" className="btn btn-primary" disabled={pwSaving}>
              {pwSaving ? 'Changing…' : 'Change Password'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
