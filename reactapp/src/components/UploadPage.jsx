import React, { useState } from 'react';
import { uploadDocument } from '../api';

function UploadPage() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleFileChange = (e) => {
    setFile(e.target.files[0] || null);
    setMessage(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setMessage({ type: 'error', text: 'Choose a file before uploading.' });
      return;
    }
    setSubmitting(true);
    setMessage(null);
    try {
      await uploadDocument(file);
      setMessage({ type: 'success', text: `"${file.name}" was uploaded successfully.` });
      setFile(null);
      e.target.reset();
    } catch (err) {
      console.error(err);
      setMessage({ type: 'error', text: 'The upload failed. Please try again.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="deposit-card">
      <h2>Upload a document</h2>
      <p className="sub">Choose a file to add it to your locker.</p>

      <form onSubmit={handleSubmit}>
        <div className="drop-zone">
          <input type="file" onChange={handleFileChange} />
          <p className="hint">Any file type · stored securely</p>
        </div>

        <button type="submit" className="btn btn-primary" data-tag="01" disabled={submitting}>
          {submitting ? 'Uploading…' : 'Upload Document'}
        </button>
      </form>

      {message && (
        <div className={`status-msg ${message.type}`}>{message.text}</div>
      )}
    </div>
  );
}

export default UploadPage;