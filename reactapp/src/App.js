import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

// Public
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';

// User
import Dashboard from './components/Dashboard';
import DocumentList from './components/DocumentList';
import DocumentDetail from './components/DocumentDetail';
import UploadPage from './components/UploadPage';
import CreateFolder from './components/CreateFolder';
import TrashPage from './components/TrashPage';
import ActivityPage from './components/ActivityPage';
import ProfilePage from './components/ProfilePage';

// Admin
import AdminDashboard from './components/admin/AdminDashboard';
import AdminUsers from './components/admin/AdminUsers';
import AdminDocuments from './components/admin/AdminDocuments';
import AdminActivity from './components/admin/AdminActivity';
import AdminSettings from './components/admin/AdminSettings';

import './App.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="app-shell">
          <Navbar />
          <main className="app-container">
            <Routes>
              {/* Public routes */}
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* User protected routes */}
              <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/documents" element={<ProtectedRoute><DocumentList /></ProtectedRoute>} />
              <Route path="/documents/upload" element={<ProtectedRoute><UploadPage /></ProtectedRoute>} />
              <Route path="/documents/:id" element={<ProtectedRoute><DocumentDetail /></ProtectedRoute>} />
              <Route path="/folders" element={<ProtectedRoute><CreateFolder /></ProtectedRoute>} />
              <Route path="/trash" element={<ProtectedRoute><TrashPage /></ProtectedRoute>} />
              <Route path="/activity" element={<ProtectedRoute><ActivityPage /></ProtectedRoute>} />
              <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />

              {/* Legacy /upload alias */}
              <Route path="/upload" element={<Navigate to="/documents/upload" replace />} />

              {/* Admin protected routes */}
              <Route path="/admin" element={<ProtectedRoute adminOnly><AdminDashboard /></ProtectedRoute>} />
              <Route path="/admin/users" element={<ProtectedRoute adminOnly><AdminUsers /></ProtectedRoute>} />
              <Route path="/admin/documents" element={<ProtectedRoute adminOnly><AdminDocuments /></ProtectedRoute>} />
              <Route path="/admin/activity" element={<ProtectedRoute adminOnly><AdminActivity /></ProtectedRoute>} />
              <Route path="/admin/settings" element={<ProtectedRoute adminOnly><AdminSettings /></ProtectedRoute>} />

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;