import React, { useEffect, useState, useCallback } from 'react';
import { adminGetUsers, adminDeleteUser, adminCreateUser, adminUpdateUser } from '../../api/client';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formUser, setFormUser] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'USER' });
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminGetUsers();
      setUsers(Array.isArray(data) ? data : (data.content || []));
    } catch (err) {
      setError(err.friendlyMessage || 'Could not load users.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async (id) => {
    setDeleting(true);
    try {
      await adminDeleteUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
      setConfirmDelete(null);
    } catch (err) {
      alert(err.friendlyMessage || 'Could not delete user.');
    } finally {
      setDeleting(false);
    }
  };

  const openForm = (user = null) => {
    setFormUser(user);
    setShowForm(true);
    setForm({ name: user?.name || '', email: user?.email || '', password: '', role: user?.role || 'USER' });
  };

  const handleSave = async (event) => {
    event.preventDefault();
    setSaving(true);
    try {
      const payload = { ...form, password: form.password || undefined };
      const saved = formUser ? await adminUpdateUser(formUser.id, payload) : await adminCreateUser(payload);
      setUsers((prev) => formUser ? prev.map((user) => user.id === saved.id ? saved : user) : [saved, ...prev]);
      setFormUser(null);
      setShowForm(false);
    } catch (err) {
      setError(err.friendlyMessage || 'Could not save user.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>User Management</h1>
        <div>
          <span className="admin-badge">ADMIN</span>
          <button className="btn btn-primary" onClick={() => openForm()}>Create User</button>
        </div>
      </div>

      {error && <div className="banner-error">{error}</div>}

      {loading ? (
        <div className="loading-block">Loading users…</div>
      ) : users.length === 0 ? (
        <div className="empty-state card"><p>No users found.</p></div>
      ) : (
        <div className="card">
          <table className="doc-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Joined</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="doc-row">
                  <td>{u.name || u.username || '—'}</td>
                  <td>{u.email}</td>
                  <td><span className={`role-badge role-badge--${(u.role || '').toLowerCase()}`}>{u.role}</span></td>
                  <td>
                    <span style={{display:'inline-flex',alignItems:'center',gap:'0.35rem',fontSize:'0.8125rem',fontWeight:600,color:u.enabled !== false ? '#059669' : '#dc2626'}}>
                      <span style={{width:7,height:7,borderRadius:'50%',background:'currentColor',display:'inline-block'}}></span>
                      {u.enabled !== false ? 'Active' : 'Disabled'}
                    </span>
                  </td>
                  <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}</td>
                  <td className="doc-actions">
                    <button
                      className="action-btn"
                      title="Edit user"
                      onClick={() => openForm(u)}
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 1 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
                    </button>
                    <button
                      className="action-btn action-btn--danger"
                      title="Delete user"
                      onClick={() => setConfirmDelete(u)}
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
            <h3>Delete user?</h3>
            <p>
              Permanently delete <strong>{confirmDelete.name || confirmDelete.email}</strong>?
              All their data will be removed. This cannot be undone.
            </p>
            <div className="modal-actions">
              <button className="btn btn-danger" disabled={deleting} onClick={() => handleDelete(confirmDelete.id)}>
                {deleting ? 'Deleting…' : 'Delete User'}
              </button>
              <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {showForm ? (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <form className="modal-box" onSubmit={handleSave}>
            <h3>{formUser ? 'Edit user' : 'Create user'}</h3>
            <div className="field"><label htmlFor="admin-user-name">Name</label><input id="admin-user-name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></div>
            <div className="field"><label htmlFor="admin-user-email">Email</label><input id="admin-user-email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></div>
            <div className="field"><label htmlFor="admin-user-password">Password {formUser && '(leave blank to keep current)'}</label><input id="admin-user-password" type="password" minLength="6" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required={!formUser} /></div>
            <div className="field"><label htmlFor="admin-user-role">Role</label><select id="admin-user-role" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}><option value="USER">User</option><option value="ADMIN">Admin</option></select></div>
            <div className="modal-actions"><button className="btn btn-primary" disabled={saving}>{saving ? 'Saving...' : 'Save User'}</button><button type="button" className="btn btn-secondary" onClick={() => { setFormUser(null); setShowForm(false); }}>Cancel</button></div>
          </form>
        </div>
      ) : null}

    </div>
  );
}
