import React, { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { login as apiLogin, register as apiRegister, getCurrentUser } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('dls_user');
    try {
      return stored ? JSON.parse(stored) : null;
    } catch {
      localStorage.removeItem('dls_user');
      return null;
    }
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const persistSession = (token, userRecord) => {
    localStorage.setItem('dls_jwt', token);
    localStorage.setItem('dls_user', JSON.stringify(userRecord));
    setUser(userRecord);
  };

  useEffect(() => {
    const handleUnauthorized = () => {
      setUser(null);
      if (window.location.pathname !== '/login') {
        window.location.assign('/login');
      }
    };
    window.addEventListener('dls:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('dls:unauthorized', handleUnauthorized);
  }, []);

  const signIn = useCallback(async (email, password, requestedRole = 'USER') => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiLogin(email, password, requestedRole);
      const token = data.token || data.jwt;
      if (!token) throw new Error('The server did not return an authentication token.');
      const role = (data.role || 'USER').replace(/^ROLE_/, '');
      let userRecord;
      try {
        userRecord = await getCurrentUser();
      } catch {
        userRecord = { email, role };
      }
      userRecord = {
        ...userRecord,
        role: (userRecord.role || role || 'USER').replace(/^ROLE_/, '').toUpperCase(),
      };
      persistSession(token, userRecord);
      return userRecord;
    } catch (err) {
      const message =
        err.friendlyMessage ||
        err.response?.data?.message ||
        'Invalid email or password.';
      setError(message);
      throw new Error(message);
    } finally {
      setLoading(false);
    }
  }, []);

  const signUp = useCallback(async (name, email, password, role = 'USER') => {
    setLoading(true);
    setError(null);
    try {
      await apiRegister(name, email, password, role);
      return true;
    } catch (err) {
      const message =
        err.friendlyMessage ||
        err.response?.data?.message ||
        'Could not create your account.';
      setError(message);
      throw new Error(message);
    } finally {
      setLoading(false);
    }
  }, []);

  const signOut = useCallback(() => {
    localStorage.removeItem('dls_jwt');
    localStorage.removeItem('dls_user');
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      role: user?.role || null,
      isAdmin: user?.role === 'ADMIN',
      loading,
      error,
      signIn,
      signUp,
      signOut,
    }),
    [user, loading, error, signIn, signUp, signOut]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
}