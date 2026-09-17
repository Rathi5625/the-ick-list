import React, { useState } from 'react';
import AuthForm from '../components/AuthForm';
import { useAuth } from '../context/AuthContext';

export default function SignupPage({ onNavigateToLogin, onSignupSuccess }) {
  const { login } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSignup = async ({ name, email, password }) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Failed to create account');
      }

      // Auto-login after signup to save the user an extra step
      const loginRes = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      if (loginRes.ok) {
        const loginData = await loginRes.json();
        login({
          token: loginData.token,
          userId: loginData.userId,
          email,
          name: loginData.name,
          avatarInitials: loginData.avatarInitials,
          avatarColor: loginData.avatarColor
        });
        if (onSignupSuccess) {
          onSignupSuccess(loginData);
        }
      } else {
        onNavigateToLogin();
      }
    } catch (err) {
      setError(err.message || 'An error occurred during signup');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthForm
      mode="signup"
      title="Create your account"
      subtitle="Ready to see your spending called out with zero filter?"
      illustrationSrc="/assets/signup-illustration-rounded-rectangles.png"
      submitText="Sign Up & Get Roasted"
      onSubmit={handleSignup}
      isLoading={isLoading}
      error={error}
      switchText="Already have an account?"
      switchActionText="Log in here"
      onSwitch={onNavigateToLogin}
    />
  );
}
