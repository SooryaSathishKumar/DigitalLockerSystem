import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { uploadDocument, getFolders } from '../api/client';
import { MAX_FILE_SIZE, isAllowedType, formatBytes } from '../utils/fileUtils';

export default function UploadPage() {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);
  const [dragging, setDragging] = useState(false);
  const [folders, setFolders] = useState([]);
  const [form, setForm] = useState({ description: '', tags: '', folderId: '' });
  const [fileError, setFileError] = useState('');
  const [message, setMessage] = useState(null);
  const [progress, setProgress] = useState(0);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    getFolders()
      .then((data) => setFolders(Array.isArray(data) ? data : (data.content || [])))
      .catch(() => {});
  }, []);

  const validateFile = (f) => {
    if (!f) { setFileError('Please select a file.'); return false; }
    if (f.size > MAX_FILE_SIZE) {
      setFileError(`File size exceeds the maximum allowed size of 50 MB. (${formatBytes(f.size)})`);
      return false;
    }
    if (!isAllowedType(f)) {
      setFileError('This file type is not supported. Please upload a PDF, document, image, or archive.');
      return false;
    }
    setFileError('');
    return true;
  };

  const selectFile = (f) => {
    if (!f) return;
    setFile(f);
    validateFile(f);
    setMessage(null);
    setProgress(0);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);
    const f = e.dataTransfer.files[0];
    if (f) selectFile(f);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateFile(file)) return;

    const formData = new FormData();
    formData.append('file', file);
    if (form.tags) formData.append('tags', form.tags);
    if (form.folderId) formData.append('parentFolderId', form.folderId);

    setSubmitting(true);
    setMessage(null);
    setProgress(0);

    try {
      await uploadDocument(formData, (progressEvent) => {
        const pct = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        setProgress(pct);
      });
      setMessage({ type: 'success', text: `"${file.name}" uploaded successfully.` });
      setFile(null);
      setForm({ description: '', tags: '', folderId: '' });
      setProgress(0);
      if (fileInputRef.current) fileInputRef.current.value = '';
    } catch (err) {
      setMessage({ type: 'error', text: err.friendlyMessage || 'Upload failed. Please try again.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="upload-page">
      <div className="page-header">
        <h1>Upload Document</h1>
      </div>

      <div className="card upload-card">
        <form onSubmit={handleSubmit} noValidate>
          {/* Drop zone */}
          <div
            id="upload-dropzone"
            className={`drop-zone ${dragging ? 'drop-zone--active' : ''} ${file ? 'drop-zone--has-file' : ''}`}
            onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
            onDragLeave={() => setDragging(false)}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
            aria-label="Click or drag to upload file"
          >
            <input
              ref={fileInputRef}
              type="file"
              style={{ display: 'none' }}
              onChange={(e) => selectFile(e.target.files[0])}
            />
            {file ? (
              <div className="drop-zone-selected">
                <span className="drop-file-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                      <polyline points="14 2 14 8 20 8"/>
                      <line x1="16" y1="13" x2="8" y2="13"/>
                      <line x1="16" y1="17" x2="8" y2="17"/>
                    </svg>
                  </span>
                <div>
                  <p className="drop-file-name">{file.name}</p>
                  <p className="drop-file-size">{formatBytes(file.size)}</p>
                </div>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={(e) => { e.stopPropagation(); setFile(null); setFileError(''); setProgress(0); }}
                >
                  Remove
                </button>
              </div>
            ) : (
              <div className="drop-zone-prompt">
                <span className="drop-icon">
                    <svg width="36" height="36" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                      <polyline points="16 16 12 12 8 16"/>
                      <line x1="12" y1="12" x2="12" y2="21"/>
                      <path d="M20.39 18.39A5 5 0 0 0 18 9h-1.26A8 8 0 1 0 3 16.3"/>
                    </svg>
                  </span>
                <p>Drag &amp; drop a file here, or <u>click to browse</u></p>
                <p className="drop-hint">Max 50 MB · PDF, Word, Excel, images, archives</p>
              </div>
            )}
          </div>
          {fileError && <span className="field-error">{fileError}</span>}

          {/* Metadata fields */}
          <div className="field" style={{ marginTop: '1.25rem' }}>
            <label htmlFor="upload-desc">Description (optional)</label>
            <textarea
              id="upload-desc"
              rows={3}
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Brief description of the document…"
            />
          </div>

          <div className="field">
            <label htmlFor="upload-tags">Tags (optional)</label>
            <input
              id="upload-tags"
              type="text"
              value={form.tags}
              onChange={(e) => setForm({ ...form, tags: e.target.value })}
              placeholder="e.g. tax, 2024, invoice (comma-separated)"
            />
          </div>

          <div className="field">
            <label htmlFor="upload-folder">Folder (optional)</label>
            <select
              id="upload-folder"
              value={form.folderId}
              onChange={(e) => setForm({ ...form, folderId: e.target.value })}
            >
              <option value="">— No folder —</option>
              {folders.map((f) => (
                <option key={f.id} value={f.id}>{f.name}</option>
              ))}
            </select>
          </div>

          {/* Progress bar */}
          {submitting && (
            <div className="progress-wrap">
              <div className="progress-track">
                <div className="progress-fill" style={{ width: `${progress}%` }} />
              </div>
              <p className="progress-label">{progress}%</p>
            </div>
          )}

          {message && (
            <div className={`banner-${message.type}`}>{message.text}</div>
          )}

          <div className="upload-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting || !file}>
              {submitting ? 'Uploading…' : 'Upload Document'}
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/documents')}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}