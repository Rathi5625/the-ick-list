import React, { useState } from 'react';
import { Eye, EyeOff, Loader2, User } from 'lucide-react';
import ErrorBanner from './ErrorBanner';

export default function AuthForm({
  mode = 'signup', // 'signup' or 'login'
  title,
  subtitle,
  illustrationSrc,
  submitText,
  onSubmit,
  isLoading,
  error,
  switchText,
  switchActionText,
  onSwitch
}) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) return;
    if (mode === 'signup' && !name) return;

    if (mode === 'signup') {
      onSubmit({ name, email, password });
    } else {
      onSubmit({ email, password });
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      padding: '24px 16px',
      width: '100%'
    }}>
      <div className="clay-card" style={{
        maxWidth: '440px',
        width: '100%',
        padding: '36px 28px',
        textAlign: 'center'
      }}>
        {illustrationSrc && (
          <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'center' }}>
            <img
              src={illustrationSrc}
              alt="Decoration"
              style={{
                width: '88px',
                height: '88px',
                objectFit: 'contain',
                filter: 'drop-shadow(0 4px 8px rgba(0,0,0,0.25))'
              }}
            />
          </div>
        )}

        <h1 style={{ fontSize: '26px', marginBottom: '8px' }}>{title}</h1>
        {subtitle && (
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '24px' }}>
            {subtitle}
          </p>
        )}

        <ErrorBanner message={error} />

        <form onSubmit={handleSubmit} style={{ textAlign: 'left', display: 'flex', flexDirection: 'column', gap: '18px' }}>
          {mode === 'signup' && (
            <div>
              <label style={{
                display: 'block',
                fontSize: '13px',
                fontWeight: 600,
                marginBottom: '6px',
                color: 'var(--text-primary)'
              }}>
                Full Name
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Parth Rathi"
                required
                className="clay-input"
                autoComplete="name"
              />
            </div>
          )}

          <div>
            <label style={{
              display: 'block',
              fontSize: '13px',
              fontWeight: 600,
              marginBottom: '6px',
              color: 'var(--text-primary)'
            }}>
              Email Address
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              required
              className="clay-input"
              autoComplete="email"
            />
          </div>

          <div>
            <label style={{
              display: 'block',
              fontSize: '13px',
              fontWeight: 600,
              marginBottom: '6px',
              color: 'var(--text-primary)'
            }}>
              Password
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="clay-input"
                style={{ paddingRight: '40px' }}
                autoComplete={mode === 'signup' ? 'new-password' : 'current-password'}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute',
                  right: '12px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'none',
                  border: 'none',
                  color: 'var(--text-secondary)',
                  cursor: 'pointer',
                  padding: '4px'
                }}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading || !email || !password || (mode === 'signup' && !name)}
            className="clay-btn"
            style={{ width: '100%', marginTop: '8px', padding: '14px', fontSize: '15px' }}
          >
            {isLoading ? (
              <>
                <Loader2 size={18} style={{ animation: 'spin 1s linear infinite' }} />
                <span>Processing...</span>
              </>
            ) : (
              <span>{submitText}</span>
            )}
          </button>
        </form>

        {(switchText || switchActionText) && (
          <div style={{ marginTop: '24px', fontSize: '13px', color: 'var(--text-secondary)' }}>
            {switchText}{' '}
            <button
              type="button"
              onClick={onSwitch}
              style={{
                background: 'none',
                border: 'none',
                color: 'var(--accent)',
                fontWeight: 600,
                cursor: 'pointer',
                textDecoration: 'underline'
              }}
            >
              {switchActionText}
            </button>
          </div>
        )}
      </div>

      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
