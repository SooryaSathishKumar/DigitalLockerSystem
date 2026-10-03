import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="landing-page">
      {/* Hero Section */}
      <section className="hero">
        <h1 className="hero-title">Digital Locker</h1>

        <p className="hero-subtitle">
          Your personal, bank-grade digital vault in the cloud.
        </p>

        <p className="lede">
          Manage your documents securely. Upload, organise, and access your critical
          certificates, KYC proofs, and important files anytime.
        </p>

        <div className="hero-cta">
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className="btn btn-primary btn-lg">
                Go to Dashboard
              </Link>
              <Link to="/documents/upload" className="btn btn-secondary btn-lg">
                Upload Document
              </Link>
            </>
          ) : (
            <>
              <Link to="/register" className="btn btn-primary btn-lg">
                Get Started
              </Link>
              <Link to="/login" className="btn btn-secondary btn-lg">
                Sign In
              </Link>
              <Link to="/documents/upload" className="btn btn-outline btn-lg">
                Upload Document
              </Link>
            </>
          )}
        </div>

        {/* 3 Core Feature Cards */}
        <div className="hero-features">
          <div className="feature-card">
            <div className="feature-icon-wrapper feature-icon--blue">
              <svg className="feature-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </div>
            <h3>Secure</h3>
            <p>JWT-authenticated, encrypted storage for all your documents.</p>
            <div className="feature-tag">256-Bit AES</div>
          </div>

          <div className="feature-card">
            <div className="feature-icon-wrapper feature-icon--amber">
              <svg className="feature-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
              </svg>
            </div>
            <h3>Organised</h3>
            <p>Folder management, tags, and powerful search to keep things tidy.</p>
            <div className="feature-tag">Smart Folders</div>
          </div>

          <div className="feature-card">
            <div className="feature-icon-wrapper feature-icon--emerald">
              <svg className="feature-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
              </svg>
            </div>
            <h3>Instant Access</h3>
            <p>Upload, view, and download documents from any device.</p>
            <div className="feature-tag">Cloud Sync</div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default Home;