import React from 'react';
import { Link } from 'react-router-dom';

const LINKS = [
  { to: '/', label: 'Home' },
  { to: '/upload', label: 'Upload' },
  { to: '/documents', label: 'Documents' },
];

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-brand">Digital Locker</Link>
        <ul className="navbar-links">
          {LINKS.map((link) => (
            <li key={link.to}>
              <Link to={link.to} className="navbar-link">{link.label}</Link>
            </li>
          ))}
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;