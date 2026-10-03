import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminGetUsers, adminGetDocuments, adminGetActivity } from '../../api/client';
import { formatDateTime } from '../../utils/fileUtils';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [activity, setActivity] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    Promise.allSettled([adminGetUsers(), adminGetDocuments(), adminGetActivity({ page: 0, size: 10 })])
      .then(([u, d, a]) => {
        if (!active) return;
        if (u.status === 'fulfilled' || d.status === 'fulfilled') {
          const users = u.status === 'fulfilled' ? u.value : {};
          const documents = d.status === 'fulfilled' ? d.value : [];
          setStats({
            totalUsers: users.totalElements ?? users.length ?? 0,
            totalDocuments: documents.length ?? 0,
          });
        }
        if (a.status === 'fulfilled') {
          const data = a.value;
          setActivity(Array.isArray(data) ? data.slice(0, 10) : (data.content || []).slice(0, 10));
        }
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>Admin Dashboard</h1>
        <span className="admin-badge">ADMIN</span>
      </div>

      <div className="stat-grid">
        <div className="stat-card">
          <span className="stat-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          </span>
          <div>
            <p className="stat-label">Total Users</p>
            <p className="stat-value">{loading ? '—' : (stats?.totalUsers ?? '—')}</p>
          </div>
        </div>
        <div className="stat-card">
          <span className="stat-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
          </span>
          <div>
            <p className="stat-label">Total Documents</p>
            <p className="stat-value">{loading ? '—' : (stats?.totalDocuments ?? '—')}</p>
          </div>
        </div>
        <div className="stat-card">
          <span className="stat-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/></svg>
          </span>
          <div>
            <p className="stat-label">System Storage</p>
            <p className="stat-value">{loading ? '—' : 'Available in storage view'}</p>
          </div>
        </div>
      </div>

      <div className="admin-links" style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', margin: '1.5rem 0' }}>
        <Link to="/admin/users" className="btn btn-secondary">Manage Users</Link>
        <Link to="/admin/documents" className="btn btn-secondary">All Documents</Link>
        <Link to="/admin/activity" className="btn btn-secondary">Activity Logs</Link>
        <Link to="/admin/settings" className="btn btn-secondary">Storage Settings</Link>
      </div>

      <div className="card dashboard-section">
        <h2>Recent System Activity</h2>
        {loading ? (
          <p className="loading-text">Loading…</p>
        ) : activity.length === 0 ? (
          <p className="empty-state">No activity yet.</p>
        ) : (
          <table className="doc-table activity-table">
            <thead>
              <tr><th>User</th><th>Action</th><th>Document</th><th>Time</th></tr>
            </thead>
            <tbody>
              {activity.map((act, i) => (
                <tr key={act.id ?? i} className="doc-row">
                  <td>{act.userName || act.userEmail || '—'}</td>
                  <td><span className="activity-badge">{act.action}</span></td>
                  <td>{act.documentName || '—'}</td>
                  <td>{formatDateTime(act.timestamp)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
