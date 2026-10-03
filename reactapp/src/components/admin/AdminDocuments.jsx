import React, { useEffect, useState, useCallback } from 'react';
import { adminGetDocuments, downloadDocument } from '../../api/client';
import { formatBytes, formatDate } from '../../utils/fileUtils';

export default function AdminDocuments() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminGetDocuments(search ? { search } : {});
      setDocs(Array.isArray(data) ? data : (data.content || []));
    } catch (err) {
      setError(err.friendlyMessage || 'Could not load documents.');
    } finally {
      setLoading(false);
    }
  }, [search]);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>All Documents</h1>
        <span className="admin-badge">ADMIN</span>
      </div>

      <div className="filter-bar">
        <input
          id="admin-doc-search"
          type="text"
          className="filter-input"
          placeholder="Search documents…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading ? (
        <div className="loading-block">Loading…</div>
      ) : docs.length === 0 ? (
        <div className="empty-state card"><p>No documents found.</p></div>
      ) : (
        <div className="card">
          <table className="doc-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Owner</th>
                <th>Type</th>
                <th>Size</th>
                <th>Uploaded</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {docs.map((doc) => (
                <tr key={doc.id} className="doc-row">
                  <td>{doc.fileName || doc.name}</td>
                  <td>{doc.user?.name || doc.user?.email || '—'}</td>
                  <td><span className="doc-type-badge">{doc.fileType || '—'}</span></td>
                  <td>{formatBytes(doc.fileSize || doc.size)}</td>
                  <td>{formatDate(doc.createdAt || doc.uploadedAt)}</td>
                  <td>{doc.status || 'ACTIVE'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
