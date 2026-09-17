import React, { useState } from 'react';
import AuthForm from '../components/AuthForm';
import { useAuth } from '../context/AuthContext';

export default function LoginPage({ onNavigateToSignup, onLoginSuccess }) {
  const { login } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleLogin = async ({ email, password }) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Invalid email or password');
      }

      login({
        token: data.token,
        userId: data.userId,
        email,
        name: data.name,
        avatarInitials: data.avatarInitials,
        avatarColor: data.avatarColor
      });

      if (onLoginSuccess) {
        onLoginSuccess(data);
      }
    } catch (err) {
      setError(err.message || 'An error occurred during login');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthForm
      mode="login"
      title="Welcome back"
      subtitle="Log in to view your roast history or analyze a new statement"
      illustrationSrc="/assets/login-illustration-concentric-circles.png"
      submitText="Log In"
      onSubmit={handleLogin}
      isLoading={isLoading}
      error={error}
      switchText="Don't have an account yet?"
      switchActionText="Sign up now"
      onSwitch={onNavigateToSignup}
    />
  );
}
