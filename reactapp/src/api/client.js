import axios from 'axios';

const BASE_URL = process.env.REACT_APP_API_BASE_URL || '';

const client = axios.create
  ? (axios.create({
      baseURL: BASE_URL,
      headers: { 'Content-Type': 'application/json' },
    }) || axios)
  : axios;

// ─── Request interceptor — attach JWT ────────────────────────────────────────
if (client.interceptors) {
  client.interceptors.request.use((config) => {
    const token = localStorage.getItem('dls_jwt');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });

  // ─── Response interceptor — friendly error messages ──────────────────────────
  client.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error.response?.status;
      if (status === 401) {
        localStorage.removeItem('dls_jwt');
        localStorage.removeItem('dls_user');
        window.dispatchEvent(new Event('dls:unauthorized'));
        error.friendlyMessage = 'Your session has expired. Please log in again.';
      } else if (status === 403) {
        error.friendlyMessage = 'You do not have permission to perform this action.';
      } else if (status === 404) {
        error.friendlyMessage = 'Resource not found.';
      } else if (status === 413) {
        error.friendlyMessage = 'File is too large. Maximum allowed size is 50 MB.';
      } else if (status >= 500) {
        error.friendlyMessage = 'Something went wrong. Please try again.';
      } else {
        error.friendlyMessage =
          error.response?.data?.message || error.message || 'An unexpected error occurred.';
      }
      return Promise.reject(error);
    }
  );
}

// ─── Auth ─────────────────────────────────────────────────────────────────────
export const login = (email, password, role = 'USER') =>
  client.post('/api/auth/login', { email, password, role }).then((r) => r.data);

export const register = (name, email, password, role = 'USER') =>
  client.post('/api/auth/register', { name, email, password, role }).then((r) => r.data);

// ─── Current user ─────────────────────────────────────────────────────────────
export const getCurrentUser = () =>
  client.get('/api/users/me').then((r) => r.data);

export const updateProfile = (data) =>
  client.put('/api/users/me', data).then((r) => r.data);

export const changePassword = (data) =>
  client.put('/api/users/me/password', data).then((r) => r.data);

// ─── Documents ────────────────────────────────────────────────────────────────
export const getDocuments = (params) =>
  client.get('/api/documents', { params }).then((r) => r.data);

export const getDocument = (id) =>
  client.get(`/api/documents/${id}`).then((r) => r.data);

export const uploadDocument = (formData, onUploadProgress) =>
  client.post('/api/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress,
  }).then((r) => r.data);

export const updateDocument = (id, data) =>
  client.put(`/api/documents/${id}`, data).then((r) => r.data);

export const deleteDocument = (id) =>
  client.delete(`/api/documents/${id}`).then((r) => r.data);

export const downloadDocument = (id) =>
  client.get(`/api/documents/${id}/download`, { responseType: 'blob' });

export const archiveDocument = (id) =>
  client.delete(`/api/documents/${id}`).then((r) => r.data);

export const restoreDocument = (id) =>
  client.put(`/api/archive/documents/${id}/restore`).then((r) => r.data);

export const getArchivedDocuments = () =>
  client.get('/api/archive/documents').then((r) => r.data);

export const permanentlyDeleteDocument = (id) =>
  client.delete(`/api/archive/documents/${id}/permanent`).then((r) => r.data);

// ─── Folders ──────────────────────────────────────────────────────────────────
export const getFolders = () =>
  client.get('/api/folders').then((r) => r.data);

export const createFolder = (name) =>
  client.post('/api/folders', { name }).then((r) => r.data);

export const updateFolder = (id, name) =>
  client.put(`/api/folders/${id}`, { name }).then((r) => r.data);

export const deleteFolder = (id) =>
  client.delete(`/api/folders/${id}`).then((r) => r.data);

// ─── Activity ─────────────────────────────────────────────────────────────────
export const getActivity = (params) =>
  client.get('/api/activity-logs', { params }).then((r) => r.data);

// ─── Storage quota ────────────────────────────────────────────────────────────
export const getStorageQuota = () =>
  client.get('/api/storage/usage').then((r) => r.data);

// ─── Admin ────────────────────────────────────────────────────────────────────
export const adminGetUsers = () =>
  client.get('/api/admin/users').then((r) => r.data);

export const adminDeleteUser = (id) =>
  client.delete(`/api/admin/users/${id}`).then((r) => r.data);

export const adminGetDocuments = (params) =>
  client.get('/api/admin/documents', { params }).then((r) => r.data);

export const adminGetActivity = (params) =>
  client.get('/api/admin/activity-logs', { params }).then((r) => r.data);

export const adminCreateUser = (payload) =>
  client.post('/api/admin/users', payload).then((r) => r.data);

export const adminUpdateUser = (id, payload) =>
  client.put(`/api/admin/users/${id}`, payload).then((r) => r.data);

export const adminResetPassword = (id, newPassword) =>
  client.post(`/api/admin/users/${id}/reset-password`, { newPassword }).then((r) => r.data);

export const adminGetStorageSettings = () =>
  client.get('/api/admin/storage/settings').then((r) => r.data);

export const adminConfigureQuota = (quota) =>
  client.put('/api/admin/storage/quota', { quota }).then((r) => r.data);

export const adminConfigureMaxFileSize = (maxFileSize) =>
  client.put('/api/admin/storage/max-file-size', { maxFileSize }).then((r) => r.data);

export default client;