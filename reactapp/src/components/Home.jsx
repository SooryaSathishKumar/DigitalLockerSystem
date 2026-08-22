import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <section className="hero">
      <p className="hero-eyebrow">Secure document storage</p>
      <h1>Digital Locker</h1>
      <p className="lede">Manage your documents securely.</p>
      <Link to="/upload" className="btn btn-primary">Upload Document</Link>
    </section>
  );
}

export default Home;