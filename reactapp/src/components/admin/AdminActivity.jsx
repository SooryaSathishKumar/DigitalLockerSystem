import React, { useEffect, useState, useCallback } from 'react';
import { adminGetActivity } from '../../api/client';
import { formatDateTime } from '../../utils/fileUtils';

export default function AdminActivity() {
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const load = useCallback(async (p = 0) => {
    setLoading(true);
    try {
      const data = await adminGetActivity({ page: p, size: 25, sort: 'timestamp,desc' });
      const items = Array.isArray(data) ? data : (data.content || []);
      if (p === 0) {
        setActivities(items);
      } else {
        setActivities((prev) => [...prev, ...items]);
      }
      setHasMore(items.length === 25);
    } catch (err) {
      setError(err.friendlyMessage || 'Could not load activity logs.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(0); }, [load]);

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>Activity Logs</h1>
        <span className="admin-badge">ADMIN</span>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading && activities.length === 0 ? (
        <div className="loading-block">Loading…</div>
      ) : activities.length === 0 ? (
        <div className="empty-state card"><p>No activity recorded.</p></div>
      ) : (
        <>
          <div className="card">
            <table className="doc-table activity-table">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Action</th>
                  <th>Document</th>
                  <th>Date &amp; Time</th>
                </tr>
              </thead>
              <tbody>
                {activities.map((act, i) => (
                  <tr key={act.id ?? i} className="doc-row">
                    <td>{act.user?.name || act.user?.email || '—'}</td>
                    <td><span className="activity-badge activity-badge--table">{act.action}</span></td>
                    <td>{act.document?.fileName || '—'}</td>
                    <td>{formatDateTime(act.timestamp)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {hasMore && (
            <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
              <button className="btn btn-secondary" disabled={loading}
                onClick={() => { const next = page + 1; setPage(next); load(next); }}>
                {loading ? 'Loading…' : 'Load more'}
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
