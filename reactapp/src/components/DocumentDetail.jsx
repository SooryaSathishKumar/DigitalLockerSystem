import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getDocument, downloadDocument, archiveDocument, deleteDocument, updateDocument, getFolders } from '../api/client';
import { formatBytes, formatDate, formatDateTime } from '../utils/fileUtils';

export default function DocumentDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [doc, setDoc] = useState(null);
  const [folders, setFolders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(false);
  const [editForm, setEditForm] = useState({ fileName: '', tags: '', folderId: '' });
  const [saving, setSaving] = useState(false);
  const [actionLoading, setActionLoading] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const [d, f] = await Promise.all([getDocument(id), getFolders()]);
        if (!active) return;
        setDoc(d);
        setEditForm({
          fileName: d.fileName || d.name || '',
          tags: Array.isArray(d.tags) ? d.tags.join(', ') : (d.tags || ''),
          folderId: d.folder?.id || d.folderId || '',
        });
        setFolders(Array.isArray(f) ? f : (f.content || []));
      } catch (err) {
        if (active) setError(err.friendlyMessage || 'Document not found.');
      } finally {
        if (active) setLoading(false);
      }
    }
    load();
    return () => { active = false; };
  }, [id]);

  const handleDownload = async () => {
    setActionLoading('download');
    try {
      const res = await downloadDocument(id);
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
      setActionLoading('');
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const updated = await updateDocument(id, {
        name: editForm.fileName,
        tags: editForm.tags,
        parentFolderId: editForm.folderId || null,
      });
      setDoc({ ...doc, ...updated });
      setEditing(false);
    } catch (err) {
      alert(err.friendlyMessage || 'Could not save changes.');
    } finally {
      setSaving(false);
    }
  };

  const handleArchive = async () => {
    setActionLoading('archive');
    try {
      await archiveDocument(id);
      navigate('/documents');
    } catch (err) {
      alert(err.friendlyMessage || 'Could not archive document.');
      setActionLoading('');
    }
  };

  const handleDelete = async () => {
    setActionLoading('delete');
    try {
      await deleteDocument(id);
      navigate('/documents');
    } catch (err) {
      alert(err.friendlyMessage || 'Could not delete document.');
      setActionLoading('');
    }
  };

  if (loading) return <div className="loading-block">Loading document…</div>;
  if (error) return (
    <div className="error-page">
      <div className="banner-error">{error}</div>
      <Link to="/documents" className="btn btn-secondary" style={{ marginTop: '1rem' }}>← Back to Documents</Link>
    </div>
  );

  return (
    <div className="doc-detail-page">
      <div className="page-header">
        <Link to="/documents" className="back-link">← Documents</Link>
        <div className="doc-detail-actions">
          <button className="btn btn-primary btn-sm" disabled={actionLoading === 'download'} onClick={handleDownload}>
            {actionLoading === 'download' ? 'Downloading…' : <><svg width="14" height="14" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{marginRight:'0.35rem'}}><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>Download</>}
          </button>
          <button className="btn btn-secondary btn-sm" onClick={() => setEditing(!editing)}>
            {editing ? 'Cancel Edit' : <><svg width="14" height="14" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{marginRight:'0.35rem'}}><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>Edit</>}
          </button>
          <button className="btn btn-secondary btn-sm" disabled={actionLoading === 'archive'} onClick={handleArchive}>
            {actionLoading === 'archive' ? 'Archiving…' : <><svg width="14" height="14" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{marginRight:'0.35rem'}}><polyline points="21 8 21 21 3 21 3 8"/><rect x="1" y="3" width="22" height="5"/><line x1="10" y1="12" x2="14" y2="12"/></svg>Archive</>}
          </button>
          <button className="btn btn-danger btn-sm" onClick={() => setConfirmDelete(true)}>
            <svg width="14" height="14" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{marginRight:'0.35rem'}}><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>Delete
          </button>
        </div>
      </div>

      <div className="card doc-detail-card">
        {editing ? (
          <div className="doc-edit-form">
            <h2>Edit Document</h2>
            <div className="field">
              <label htmlFor="edit-name">File Name</label>
              <input id="edit-name" type="text" value={editForm.fileName}
                onChange={(e) => setEditForm({ ...editForm, fileName: e.target.value })} />
            </div>
            <div className="field">
              <label htmlFor="edit-tags">Tags (comma-separated)</label>
              <input id="edit-tags" type="text" value={editForm.tags}
                onChange={(e) => setEditForm({ ...editForm, tags: e.target.value })} />
            </div>
            <div className="field">
              <label htmlFor="edit-folder">Folder</label>
              <select id="edit-folder" value={editForm.folderId}
                onChange={(e) => setEditForm({ ...editForm, folderId: e.target.value })}>
                <option value="">— No folder —</option>
                {folders.map((f) => <option key={f.id} value={f.id}>{f.name}</option>)}
              </select>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button className="btn btn-primary" disabled={saving} onClick={handleSave}>
                {saving ? 'Saving…' : 'Save Changes'}
              </button>
              <button className="btn btn-secondary" onClick={() => setEditing(false)}>Cancel</button>
            </div>
          </div>
        ) : (
          <>
            <h1 className="doc-detail-name">{doc.fileName || doc.name}</h1>
            <div className="doc-meta-grid">
              <div className="doc-meta-item"><span className="meta-label">File Type</span><span>{doc.fileType || '—'}</span></div>
              <div className="doc-meta-item"><span className="meta-label">Size</span><span>{formatBytes(doc.fileSize || doc.size)}</span></div>
              <div className="doc-meta-item"><span className="meta-label">Folder</span><span>{folders.find((folder) => String(folder.id) === String(doc.parentFolderId))?.name || '—'}</span></div>
              <div className="doc-meta-item"><span className="meta-label">Status</span><span className="doc-status">{doc.status || 'Active'}</span></div>
              <div className="doc-meta-item"><span className="meta-label">Uploaded</span><span>{formatDateTime(doc.createdAt || doc.uploadedAt)}</span></div>
              <div className="doc-meta-item"><span className="meta-label">Last Updated</span><span>{formatDate(doc.updatedAt)}</span></div>
            </div>
            {doc.description && (
              <div className="doc-description">
                <h3>Description</h3>
                <p>{doc.description}</p>
              </div>
            )}
            {(doc.tags && (Array.isArray(doc.tags) ? doc.tags.length > 0 : doc.tags)) && (
              <div className="doc-tags">
                <h3>Tags</h3>
                <div className="tag-list">
                  {(Array.isArray(doc.tags) ? doc.tags : doc.tags.split(',')).map((t, i) => (
                    <span key={i} className="tag-badge">{t.trim()}</span>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {confirmDelete && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-box">
            <h3>Delete document?</h3>
            <p>This will permanently delete <strong>{doc.fileName || doc.name}</strong>. This cannot be undone.</p>
            <div className="modal-actions">
              <button className="btn btn-danger" disabled={actionLoading === 'delete'} onClick={handleDelete}>
                {actionLoading === 'delete' ? 'Deleting…' : 'Delete'}
              </button>
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
