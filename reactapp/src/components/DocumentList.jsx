import React, { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  getDocuments, deleteDocument, archiveDocument, downloadDocument, getFolders
} from '../api/client';
import { formatBytes, formatDate } from '../utils/fileUtils';

const FILE_TYPES = ['All', 'PDF', 'Word', 'Excel', 'Image', 'Text', 'Other'];

export default function DocumentList() {
  const navigate = useNavigate();
  const [docs, setDocs] = useState([]);
  const [folders, setFolders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('All');
  const [folderFilter, setFolderFilter] = useState('All');
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [actionLoading, setActionLoading] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const params = {};
      if (search) params.name = search;
      if (typeFilter !== 'All') params.type = typeFilter;
      const [data, folderData] = await Promise.all([
        getDocuments(params),
        getFolders(),
      ]);
      const list = Array.isArray(data) ? data : (data.content || []);
      setDocs(folderFilter === 'All' ? list : list.filter((doc) => String(doc.parentFolderId) === String(folderFilter)));
      setFolders(Array.isArray(folderData) ? folderData : (folderData.content || []));
    } catch (err) {
      setError(err.friendlyMessage || 'Failed to load documents.');
    } finally {
      setLoading(false);
    }
  }, [search, typeFilter, folderFilter]);

  useEffect(() => { load(); }, [load]);

  const handleDownload = async (doc) => {
    setActionLoading(doc.id + '-download');
    try {
      const res = await downloadDocument(doc.id);
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const a = document.createElement('a');
      a.href = url;
      a.download = doc.fileName || doc.name || 'download';
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(err.friendlyMessage || 'Download failed.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleArchive = async (doc) => {
    setActionLoading(doc.id + '-archive');
    try {
      await archiveDocument(doc.id);
      setDocs((prev) => prev.filter((d) => d.id !== doc.id));
    } catch (err) {
      alert(err.friendlyMessage || 'Could not archive document.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDelete = async (id) => {
    setActionLoading(id + '-delete');
    try {
      await deleteDocument(id);
      setDocs((prev) => prev.filter((d) => d.id !== id));
      setConfirmDelete(null);
    } catch (err) {
      alert(err.friendlyMessage || 'Could not delete document.');
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="doc-list-page">
      <div className="page-header">
        <h1>My Documents</h1>
        <Link to="/documents/upload" className="btn btn-primary btn-sm">Upload</Link>
      </div>

      {/* Filters */}
      <div className="filter-bar">
        <input
          id="doc-search"
          type="text"
          className="filter-input"
          placeholder="Search documents…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <select
          id="doc-type-filter"
          className="filter-select"
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value)}
        >
          {FILE_TYPES.map((t) => <option key={t}>{t}</option>)}
        </select>
        <select
          id="doc-folder-filter"
          className="filter-select"
          value={folderFilter}
          onChange={(e) => setFolderFilter(e.target.value)}
        >
          <option value="All">All Folders</option>
          {folders.map((f) => (
            <option key={f.id} value={f.id}>{f.name}</option>
          ))}
        </select>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading ? (
        <div className="loading-block">Loading documents…</div>
      ) : docs.length === 0 ? (
        <div className="empty-state card">
          <p>No documents found.</p>
          <Link to="/documents/upload" className="btn btn-primary btn-sm">Upload your first document</Link>
        </div>
      ) : (
        <div className="doc-table-wrap">
          <table className="doc-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Size</th>
                <th>Folder</th>
                <th>Uploaded</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {docs.map((doc) => (
                <tr key={doc.id} className="doc-row">
                  <td>
                    <Link to={`/documents/${doc.id}`} className="doc-name-link">
                      {doc.fileName || doc.name}
                    </Link>
                  </td>
                  <td><span className="doc-type-badge">{doc.fileType || '—'}</span></td>
                  <td>{formatBytes(doc.fileSize || doc.size)}</td>
                  <td>{folders.find((folder) => String(folder.id) === String(doc.parentFolderId))?.name || '—'}</td>
                  <td>{formatDate(doc.createdAt || doc.uploadedAt)}</td>
                  <td className="doc-actions">
                    <button
                      className="action-btn"
                      title="View"
                      onClick={() => navigate(`/documents/${doc.id}`)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    </button>
                    <button
                      className="action-btn"
                      title="Download"
                      disabled={actionLoading === doc.id + '-download'}
                      onClick={() => handleDownload(doc)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                    </button>
                    <button
                      className="action-btn"
                      title="Archive"
                      disabled={actionLoading === doc.id + '-archive'}
                      onClick={() => handleArchive(doc)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="21 8 21 21 3 21 3 8"/><rect x="1" y="3" width="22" height="5"/><line x1="10" y1="12" x2="14" y2="12"/></svg>
                    </button>
                    <button
                      className="action-btn action-btn--danger"
                      title="Delete"
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

      {/* Delete confirmation modal */}
      {confirmDelete && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-box">
            <h3>Delete document?</h3>
            <p>
              Are you sure you want to permanently delete{' '}
              <strong>{confirmDelete.fileName || confirmDelete.name}</strong>?
              This action cannot be undone.
            </p>
            <div className="modal-actions">
              <button
                className="btn btn-danger"
                disabled={actionLoading === confirmDelete.id + '-delete'}
                onClick={() => handleDelete(confirmDelete.id)}
              >
                {actionLoading === confirmDelete.id + '-delete' ? 'Deleting…' : 'Delete'}
              </button>
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}