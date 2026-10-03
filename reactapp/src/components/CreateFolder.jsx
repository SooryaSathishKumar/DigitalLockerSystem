import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { getFolders, createFolder, updateFolder, deleteFolder } from '../api/client';
import './Folder.css';

export default function CreateFolder() {
  const { isAuthenticated } = useAuth();
  const [folders, setFolders] = useState([]);
  const [folderName, setFolderName] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState('');
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(null);

  const loadFolders = useCallback(async () => {
    setFetchLoading(true);
    try {
      const data = await getFolders();
      setFolders(Array.isArray(data) ? data : (data.content || []));
    } catch (err) {
      setErrors({ general: err.friendlyMessage || 'Could not load folders.' });
    } finally {
      setFetchLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) loadFolders();
  }, [isAuthenticated, loadFolders]);

  const validate = () => {
    const next = {};
    if (!folderName.trim()) next.folderName = 'Folder name is required.';
    else if (folderName.trim().length < 3) next.folderName = 'Folder name must be at least 3 characters.';
    else if (folders.some((f) => f.name.toLowerCase() === folderName.trim().toLowerCase())) {
      next.folderName = 'A folder with this name already exists.';
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleCreateFolder = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    setMessage('');
    try {
      const newFolder = await createFolder(folderName.trim());
      setFolders((prev) => [...prev, newFolder]);
      setMessage(`Folder "${folderName.trim()}" created successfully!`);
      setFolderName('');
      setErrors({});
    } catch (err) {
      setErrors({ general: err.friendlyMessage || 'Failed to create folder.' });
    } finally {
      setLoading(false);
    }
  };

  const handleRename = async (id) => {
    if (!editName.trim()) return;
    setLoading(true);
    try {
      const updated = await updateFolder(id, editName.trim());
      setFolders((prev) => prev.map((f) => f.id === id ? { ...f, ...updated, name: editName.trim() } : f));
      setEditingId(null);
      setEditName('');
      setMessage('Folder renamed.');
    } catch (err) {
      alert(err.friendlyMessage || 'Could not rename folder.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    setLoading(true);
    try {
      await deleteFolder(id);
      setFolders((prev) => prev.filter((f) => f.id !== id));
      setConfirmDelete(null);
      setMessage('Folder deleted.');
    } catch (err) {
      alert(err.friendlyMessage || 'Could not delete folder.');
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <section className="folder-section">
        <div className="folder-empty">
          <p>Please log in to manage folders.</p>
        </div>
      </section>
    );
  }

  return (
    <section className="folder-section">
      <div className="folder-header">
        <h1>Folders</h1>
        <p>Organise your documents into folders</p>
      </div>

      <div className="folder-content">
        {/* Create Folder Form */}
        <div className="folder-form-container card">
          <h3>Create New Folder</h3>
          {message && <div className="banner-success">{message}</div>}
          {errors.general && <div className="banner-error">{errors.general}</div>}

          <form onSubmit={handleCreateFolder} noValidate>
            <div className="field">
              <label htmlFor="folderName">Folder Name</label>
              <input
                id="folderName"
                type="text"
                placeholder="e.g. Tax Documents, Medical Records"
                value={folderName}
                onChange={(e) => setFolderName(e.target.value)}
              />
              {errors.folderName && <span className="field-error">{errors.folderName}</span>}
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Creating…' : 'Create Folder'}
            </button>
          </form>
        </div>

        {/* Folders List */}
        <div className="folder-list-container">
          <h3>Your Folders ({fetchLoading ? '…' : folders.length})</h3>

          {fetchLoading ? (
            <p className="loading-text">Loading folders…</p>
          ) : folders.length === 0 ? (
            <div className="folder-empty-state">
              <p>No folders yet. Create one to get started!</p>
            </div>
          ) : (
            <div className="folder-grid">
              {folders.map((folder) => (
                <div key={folder.id} className="folder-card">
                  <div className="folder-icon">
                    <svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
                    </svg>
                  </div>

                  {editingId === folder.id ? (
                    <div className="folder-rename">
                      <input
                        type="text"
                        value={editName}
                        onChange={(e) => setEditName(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter') handleRename(folder.id);
                          if (e.key === 'Escape') setEditingId(null);
                        }}
                        autoFocus
                      />
                      <button className="btn btn-primary btn-sm" onClick={() => handleRename(folder.id)}>Save</button>
                      <button className="btn btn-secondary btn-sm" onClick={() => setEditingId(null)}>Cancel</button>
                    </div>
                  ) : (
                    <h4>{folder.name}</h4>
                  )}

                  <p className="folder-date">
                    Created: {new Date(folder.createdAt || folder.created).toLocaleDateString()}
                  </p>

                  <div className="folder-card-actions">
                    <button
                      type="button"
                      className="btn btn-secondary btn-sm"
                      onClick={() => { setEditingId(folder.id); setEditName(folder.name); }}
                    >
                      Rename
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => setConfirmDelete(folder)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Delete confirmation */}
      {confirmDelete && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-box">
            <h3>Delete folder?</h3>
            <p>
              Delete <strong>{confirmDelete.name}</strong>? Documents inside will not be deleted.
            </p>
            <div className="modal-actions">
              <button className="btn btn-danger" disabled={loading} onClick={() => handleDelete(confirmDelete.id)}>
                {loading ? 'Deleting…' : 'Delete'}
              </button>
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
