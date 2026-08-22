import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:5000/api';

export function uploadDocument(file) {
  const formData = new FormData();
  formData.append('file', file);
  return axios.post(`${API_BASE}/documents/upload`, formData);
}

export function getDocuments() {
  return axios.get(`${API_BASE}/documents`);
}