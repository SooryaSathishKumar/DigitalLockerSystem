import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { isAuthenticated, isAdmin, user, signOut } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    signOut();
    setMenuOpen(false);
    navigate('/login');
  };

  const close = () => setMenuOpen(false);

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to={isAuthenticated ? (isAdmin ? '/admin' : '/dashboard') : '/'} className="navbar-brand" onClick={close}>
          <svg width="20" height="20" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{flexShrink:0}}>
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          Digital Locker
        </Link>

        {/* Hamburger */}
        <button
          className="navbar-hamburger"
          aria-label="Toggle menu"
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((o) => !o)}
        >
          <span /><span /><span />
        </button>

        <div className={`navbar-menu ${menuOpen ? 'navbar-menu--open' : ''}`}>
          <ul className="navbar-links">
            {!isAuthenticated ? (
              <>
                <li><NavLink to="/" className="navbar-link" onClick={close}>Home</NavLink></li>
                <li><NavLink to="/login" className="navbar-link" onClick={close}>Login</NavLink></li>
                <li><NavLink to="/register" className="navbar-link" onClick={close}>Register</NavLink></li>
              </>
            ) : isAdmin ? (
              <>
                <li><NavLink to="/admin" className="navbar-link" onClick={close}>Admin Dashboard</NavLink></li>
                <li><NavLink to="/admin/users" className="navbar-link" onClick={close}>Users</NavLink></li>
                <li><NavLink to="/admin/documents" className="navbar-link" onClick={close}>Documents</NavLink></li>
                <li><NavLink to="/admin/activity" className="navbar-link" onClick={close}>Activity Logs</NavLink></li>
                <li><NavLink to="/admin/settings" className="navbar-link" onClick={close}>Settings</NavLink></li>
              </>
            ) : (
              <>
                <li><NavLink to="/dashboard" className="navbar-link" onClick={close}>Dashboard</NavLink></li>
                <li><NavLink to="/documents" className="navbar-link" onClick={close}>Documents</NavLink></li>
                <li><NavLink to="/folders" className="navbar-link" onClick={close}>Folders</NavLink></li>
                <li><NavLink to="/trash" className="navbar-link" onClick={close}>Archive</NavLink></li>
                <li><NavLink to="/activity" className="navbar-link" onClick={close}>Activity</NavLink></li>
                <li><NavLink to="/profile" className="navbar-link" onClick={close}>Profile</NavLink></li>
              </>
            )}
          </ul>

          <div className="navbar-auth">
            {isAuthenticated ? (
              <>
                <span className="navbar-user">{user?.name || user?.email}</span>
                {isAdmin && <span className="admin-badge" style={{ fontSize: '0.7rem' }}>ADMIN</span>}
                <button type="button" className="btn btn-secondary btn-sm navbar-logout" onClick={handleLogout}>
                  Logout
                </button>
              </>
            ) : null}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;