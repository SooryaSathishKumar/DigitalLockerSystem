import React, { useEffect, useState, useCallback } from 'react';
import { getActivity } from '../api/client';
import { formatDateTime } from '../utils/fileUtils';


export default function ActivityPage() {
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const load = useCallback(async (p = 0) => {
    setLoading(true);
    setError('');
    try {
      const data = await getActivity({ page: p, size: 20, sort: 'timestamp,desc' });
      const items = Array.isArray(data) ? data : (data.content || []);
      if (p === 0) {
        setActivities(items);
      } else {
        setActivities((prev) => [...prev, ...items]);
      }
      setHasMore(items.length === 20);
    } catch (err) {
      setError(err.friendlyMessage || 'Could not load activity log.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(0); }, [load]);

  const loadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    load(nextPage);
  };

  return (
    <div className="activity-page">
      <div className="page-header">
        <h1>Activity Log</h1>
        <p className="page-sub">A record of all your actions in the Digital Locker.</p>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading && activities.length === 0 ? (
        <div className="loading-block">Loading activity…</div>
      ) : activities.length === 0 ? (
        <div className="empty-state card"><p>No activity yet.</p></div>
      ) : (
        <>
          <div className="card">
            <table className="doc-table activity-table">
              <thead>
                <tr>
                  <th>Action</th>
                  <th>Document</th>
                  <th>Date &amp; Time</th>
                </tr>
              </thead>
              <tbody>
                {activities.map((act, i) => (
                  <tr key={act.id ?? i} className="doc-row">
                    <td>
                      <span className="activity-badge activity-badge--table">
                        {act.action}
                      </span>
                    </td>
                    <td>{act.document?.fileName || act.documentName || '—'}</td>
                    <td>{formatDateTime(act.timestamp)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {hasMore && (
            <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
              <button
                className="btn btn-secondary"
                disabled={loading}
                onClick={loadMore}
              >
                {loading ? 'Loading…' : 'Load more'}
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
