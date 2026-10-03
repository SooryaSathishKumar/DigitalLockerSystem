import React, { useEffect, useState, useCallback } from 'react';
import { getArchivedDocuments, restoreDocument, permanentlyDeleteDocument } from '../api/client';
import { formatBytes, formatDate } from '../utils/fileUtils';

export default function TrashPage() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [actionLoading, setActionLoading] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getArchivedDocuments();
      setDocs(Array.isArray(data) ? data : (data.content || []));
    } catch (err) {
      setError(err.friendlyMessage || 'Could not load archived documents.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleRestore = async (doc) => {
    setActionLoading(doc.id + '-restore');
    try {
      await restoreDocument(doc.id);
      setDocs((prev) => prev.filter((d) => d.id !== doc.id));
    } catch (err) {
      alert(err.friendlyMessage || 'Could not restore document.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDelete = async (id) => {
    setActionLoading(id + '-delete');
    try {
      await permanentlyDeleteDocument(id);
      setDocs((prev) => prev.filter((d) => d.id !== id));
      setConfirmDelete(null);
    } catch (err) {
      alert(err.friendlyMessage || 'Could not delete document.');
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="trash-page">
      <div className="page-header">
        <h1>Archive / Trash</h1>
        <p className="page-sub">Archived documents can be restored or permanently deleted.</p>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading ? (
        <div className="loading-block">Loading archived documents…</div>
      ) : docs.length === 0 ? (
        <div className="empty-state card">
          <p>No archived documents.</p>
        </div>
      ) : (
        <div className="doc-table-wrap">
          <table className="doc-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Size</th>
                <th>Archived</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {docs.map((doc) => (
                <tr key={doc.id} className="doc-row">
                  <td>{doc.fileName || doc.name}</td>
                  <td><span className="doc-type-badge">{doc.fileType || '—'}</span></td>
                  <td>{formatBytes(doc.fileSize || doc.size)}</td>
                  <td>{formatDate(doc.archivedAt || doc.updatedAt)}</td>
                  <td className="doc-actions">
                    <button
                      className="action-btn"
                      title="Restore"
                      disabled={actionLoading === doc.id + '-restore'}
                      onClick={() => handleRestore(doc)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
                    </button>
                    <button
                      className="action-btn action-btn--danger"
                      title="Permanently Delete"
                      onClick={() => setConfirmDelete(doc)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {confirmDelete && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-box">
            <h3>Permanently delete?</h3>
            <p>
              <strong>{confirmDelete.fileName || confirmDelete.name}</strong> will be permanently deleted.
              This action cannot be undone.
            </p>
            <div className="modal-actions">
              <button
                className="btn btn-danger"
                disabled={actionLoading === confirmDelete.id + '-delete'}
                onClick={() => handleDelete(confirmDelete.id)}
              >
                {actionLoading === confirmDelete.id + '-delete' ? 'Deleting…' : 'Delete Forever'}
              </button>
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
