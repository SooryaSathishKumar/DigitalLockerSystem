import React, { useEffect, useState } from 'react';
import {
  adminConfigureMaxFileSize,
  adminConfigureQuota,
  adminGetStorageSettings,
} from '../../api/client';

const BYTES_PER_MB = 1024 * 1024;

export default function AdminSettings() {
  const [quota, setQuota] = useState('500');
  const [maxFileSize, setMaxFileSize] = useState('50');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    adminGetStorageSettings()
      .then((settings) => {
        setQuota(String(Math.round(settings.defaultQuota / BYTES_PER_MB)));
        setMaxFileSize(String(Math.round(settings.maxFileSize / BYTES_PER_MB)));
      })
      .catch((err) => setError(err.friendlyMessage || 'Could not load storage settings.'))
      .finally(() => setLoading(false));
  }, []);

  const saveSetting = async (name, value, save) => {
    const megabytes = Number(value);
    if (!Number.isFinite(megabytes) || megabytes <= 0) {
      setError('Storage values must be greater than zero.');
      return;
    }

    setSaving(name);
    setError('');
    setMessage('');
    try {
      await save(megabytes * BYTES_PER_MB);
      setMessage(`${name === 'quota' ? 'Default quota' : 'Maximum file size'} updated.`);
    } catch (err) {
      setError(err.friendlyMessage || 'Could not update storage settings.');
    } finally {
      setSaving('');
    }
  };

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>Storage Settings</h1>
        <span className="admin-badge">ADMIN</span>
      </div>

      {error && <div className="banner-error">{error}</div>}
      {message && <div className="banner-success">{message}</div>}

      <div className="card">
        <h2>Storage limits</h2>
        <p className="auth-sub">These limits apply system-wide to every user.</p>
        {loading ? <p className="loading-block">Loading settings...</p> : (
          <>
            <div className="field">
              <label htmlFor="default-quota">Default quota per user (MB)</label>
              <input id="default-quota" type="number" min="1" step="1" value={quota} onChange={(event) => setQuota(event.target.value)} />
              <button className="btn btn-primary" disabled={saving === 'quota'} onClick={() => saveSetting('quota', quota, adminConfigureQuota)}>
                {saving === 'quota' ? 'Saving...' : 'Save quota'}
              </button>
            </div>
            <div className="field">
              <label htmlFor="max-file-size">Maximum file size (MB)</label>
              <input id="max-file-size" type="number" min="1" step="1" value={maxFileSize} onChange={(event) => setMaxFileSize(event.target.value)} />
              <button className="btn btn-primary" disabled={saving === 'maxFileSize'} onClick={() => saveSetting('maxFileSize', maxFileSize, adminConfigureMaxFileSize)}>
                {saving === 'maxFileSize' ? 'Saving...' : 'Save file limit'}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
