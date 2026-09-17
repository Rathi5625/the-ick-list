import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(null); // { token, userId, email, name, avatarInitials, avatarColor }

  const login = (userData) => {
    // Accepts object or separate arguments
    if (typeof userData === 'string') {
      // legacy signature fallback: login(token, userId, email)
      const [token, userId, email] = arguments;
      setAuth({ token, userId, email, name: email?.split('@')[0], avatarInitials: 'U', avatarColor: '#9E6752' });
    } else {
      setAuth(userData);
    }
  };

  const logout = () => {
    setAuth(null);
  };

  return (
    <AuthContext.Provider value={{
      auth,
      token: auth?.token,
      userId: auth?.userId,
      email: auth?.email,
      name: auth?.name,
      avatarInitials: auth?.avatarInitials || 'U',
      avatarColor: auth?.avatarColor || '#9E6752',
      isAuthenticated: !!auth,
      login,
      logout
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
