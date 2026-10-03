import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getDocuments, getActivity, getStorageQuota, getFolders, getArchivedDocuments } from '../api/client';
import { formatBytes, formatDateTime } from '../utils/fileUtils';

function StatIcon({ type }) {
  if (type === 'documents') {
    return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 3.75h7l3.25 3.25V20.25H7z" /><path d="M14 3.75v3.5h3.5M9.75 11h4.5M9.75 14.5h4.5M9.75 18h2.75" /></svg>;
  }
  if (type === 'folders') {
    return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3.75 7.5h6l1.75 2h8.75v9.75H3.75z" /><path d="M3.75 7.5V5.75h6l1.75 1.75" /></svg>;
  }
  if (type === 'archived') {
    return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 7.25h16v12H4zM3 4.75h18v2.5H3zM9 11h6M9 14h6" /></svg>;
  }
  return <svg viewBox="0 0 24 24" aria-hidden="true"><ellipse cx="12" cy="6" rx="7" ry="2.5" /><path d="M5 6v6c0 1.4 3.1 2.5 7 2.5s7-1.1 7-2.5V6M5 12v6c0 1.4 3.1 2.5 7 2.5s7-1.1 7-2.5v-6" /></svg>;
}

export default function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [recentDocs, setRecentDocs] = useState([]);
  const [recentActivity, setRecentActivity] = useState([]);
  const [quota, setQuota] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const [docs, acts, q, folders, archived] = await Promise.allSettled([
          getDocuments({ size: 5, sort: 'createdAt,desc' }),
          getActivity({ size: 5 }),
          getStorageQuota(),
          getFolders(),
          getArchivedDocuments(),
        ]);
        if (!active) return;
        if (docs.status === 'fulfilled') {
          const data = docs.value;
          const documents = Array.isArray(data) ? data : (data.content || []);
          setRecentDocs(documents.slice(0, 5));
          setStats((previous) => ({ ...previous, totalDocuments: data.totalElements ?? documents.length }));
        }
        if (folders.status === 'fulfilled' || archived.status === 'fulfilled') {
          setStats((previous) => ({ ...previous,
            totalFolders: folders.status === 'fulfilled' ? folders.value.length : 0,
            totalArchived: archived.status === 'fulfilled' ? archived.value.length : 0,
          }));
        }
        if (acts.status === 'fulfilled') {
          const data = acts.value;
          setRecentActivity(Array.isArray(data) ? data.slice(0, 5) : (data.content || []).slice(0, 5));
        }
        if (q.status === 'fulfilled') setQuota(q.value);
      } catch (err) {
        if (active) setError('Could not load dashboard data.');
      } finally {
        if (active) setLoading(false);
      }
    }
    load();
    return () => { active = false; };
  }, []);

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div>
          <h1>Welcome back{user?.name ? `, ${user.name}` : ''}!</h1>
          <p className="dashboard-sub">Here's an overview of your Digital Locker.</p>
        </div>
        <Link to="/documents/upload" className="btn btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          Upload Document
        </Link>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {/* Stats cards */}
      <div className="stat-grid">
        <div className="stat-card stat-card--documents">
          <div className="stat-card-top">
            <p className="stat-label">Documents</p>
            <span className="stat-icon"><StatIcon type="documents" /></span>
          </div>
          <div className="stat-card-bottom">
            <p className="stat-value">{loading ? '—' : (stats?.totalDocuments ?? recentDocs.length)}</p>
          </div>
          <p className="stat-note">Verified digital credentials &amp; files</p>
        </div>
        <div className="stat-card stat-card--folders">
          <div className="stat-card-top">
            <p className="stat-label">Folders</p>
            <span className="stat-icon"><StatIcon type="folders" /></span>
          </div>
          <div className="stat-card-bottom">
            <p className="stat-value">{loading ? '—' : (stats?.totalFolders ?? '—')}</p>
            <span className="stat-context">All organized</span>
          </div>
          <p className="stat-note">Personal, Academic, Finance, KYC</p>
        </div>
        <div className="stat-card stat-card--archived">
          <div className="stat-card-top">
            <p className="stat-label">Archived</p>
            <span className="stat-icon"><StatIcon type="archived" /></span>
          </div>
          <div className="stat-card-bottom">
            <p className="stat-value">{loading ? '—' : (stats?.totalArchived ?? '—')}</p>
            <span className="stat-context">Inactive</span>
          </div>
          <p className="stat-note">Protected in cold vault storage</p>
        </div>
        <div className="stat-card stat-card--storage">
          <div className="stat-card-top">
            <p className="stat-label">Storage Used</p>
            <span className="stat-icon"><StatIcon type="storage" /></span>
          </div>
          <div className="stat-card-bottom">
            <p className="stat-value stat-value--storage">
              {loading ? '—' : formatBytes(quota?.storageUsed ?? 0)}
            </p>
            <span className="stat-context">of {formatBytes(quota?.storageQuota ?? 524288000)}</span>
          </div>
          <p className="stat-note stat-note--success">
            {formatBytes(Math.max(0, (quota?.storageQuota ?? 524288000) - (quota?.storageUsed ?? 0)))} remaining ({Math.round(quota?.usagePercentage ?? 0)}% used)
          </p>
        </div>
      </div>

      <div className="dashboard-cols">
        {/* Recent Documents */}
        <div className="card dashboard-section">
          <div className="section-head">
            <h2>Recent Documents</h2>
            <Link to="/documents" className="section-link">View all →</Link>
          </div>
          {loading ? (
            <p className="loading-text">Loading…</p>
          ) : recentDocs.length === 0 ? (
            <div className="empty-state">
              <p>No documents yet.</p>
              <Link to="/documents/upload" className="btn btn-secondary btn-sm">Upload your first document</Link>
            </div>
          ) : (
            <ul className="recent-list">
              {recentDocs.map((doc) => (
                  <li key={doc.id} className="recent-item">
                  <span className="recent-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                      <polyline points="14 2 14 8 20 8"/>
                      <line x1="16" y1="13" x2="8" y2="13"/>
                      <line x1="16" y1="17" x2="8" y2="17"/>
                      <polyline points="10 9 9 9 8 9"/>
                    </svg>
                  </span>
                  <div className="recent-info">
                    <Link to={`/documents/${doc.id}`} className="recent-name">{doc.fileName || doc.name}</Link>
                    <span className="recent-meta">{formatDateTime(doc.createdAt || doc.uploadedAt)}</span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Recent Activity */}
        <div className="card dashboard-section">
          <div className="section-head">
            <h2>Recent Activity</h2>
            <Link to="/activity" className="section-link">View all →</Link>
          </div>
          {loading ? (
            <p className="loading-text">Loading…</p>
          ) : recentActivity.length === 0 ? (
            <p className="empty-state">No activity yet.</p>
          ) : (
            <ul className="activity-list">
              {recentActivity.map((act, i) => (
                <li key={act.id ?? i} className="activity-item">
                  <span className="activity-badge">{act.action}</span>
                  <div className="activity-info">
                    <span className="activity-doc">{act.document?.fileName || act.documentName || '—'}</span>
                    <span className="activity-time">{formatDateTime(act.timestamp)}</span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
