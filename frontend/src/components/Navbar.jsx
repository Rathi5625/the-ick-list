import React, { useState, useEffect } from 'react';
import { Sun, Moon, LogOut, FileText, Clock, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import Avatar from './Avatar';

export default function Navbar({ theme, onToggleTheme, currentView, onNavigate }) {
  const { isAuthenticated, name, email, avatarInitials, avatarColor, logout } = useAuth();
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const displayName = name || email?.split('@')[0] || 'User';

  return (
    <header style={{
      position: 'sticky',
      top: '16px',
      zIndex: 100,
      width: '100%',
      padding: '0 16px',
      display: 'flex',
      justifyContent: 'center'
    }}>
      <nav style={{
        maxWidth: '1080px',
        width: '100%',
        borderRadius: '9999px',
        padding: '10px 20px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        // Floating glassmorphism treatment
        backgroundColor: scrolled
          ? 'rgba(45, 67, 84, 0.92)'
          : 'rgba(45, 67, 84, 0.75)',
        backdropFilter: 'blur(16px)',
        WebkitBackdropFilter: 'blur(16px)',
        border: '1px solid rgba(254, 215, 165, 0.22)',
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.35), 0 2px 8px rgba(254, 215, 165, 0.05)',
        transition: 'all 0.3s cubic-bezier(0.16, 1, 0.3, 1)'
      }}>
        {/* Left: Brand logo + wordmark */}
        <div
          onClick={() => onNavigate(isAuthenticated ? 'upload' : 'signup')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            cursor: 'pointer',
            userSelect: 'none'
          }}
        >
          <img
            src="/assets/logo-mark-favicon.png"
            alt="The Ick List Logo"
            style={{
              width: '32px',
              height: '32px',
              filter: 'drop-shadow(0 2px 4px rgba(0,0,0,0.3))'
            }}
          />
          <span style={{
            fontFamily: 'var(--font-heading)',
            fontSize: '19px',
            fontWeight: 700,
            color: 'var(--color-cream)',
            letterSpacing: '-0.02em',
            whiteSpace: 'nowrap'
          }}>
            The Ick List
          </span>
        </div>

        {/* Center: Nav links in pill-shaped glass group */}
        {isAuthenticated && (
          <div style={{
            display: 'flex',
            alignItems: 'center',
            backgroundColor: 'rgba(32, 33, 43, 0.45)',
            borderRadius: '9999px',
            padding: '4px',
            border: '1px solid rgba(254, 215, 165, 0.12)'
          }}>
            <button
              onClick={() => onNavigate('upload')}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                padding: '6px 14px',
                borderRadius: '9999px',
                border: 'none',
                cursor: 'pointer',
                fontSize: '13px',
                fontWeight: 600,
                transition: 'all 0.2s ease',
                backgroundColor: currentView === 'upload' ? 'var(--accent)' : 'transparent',
                color: 'var(--color-cream)',
                boxShadow: currentView === 'upload' ? '0 2px 8px rgba(158, 103, 82, 0.4)' : 'none'
              }}
            >
              <FileText size={14} />
              <span>Upload CSV</span>
            </button>

            <button
              onClick={() => onNavigate('history')}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                padding: '6px 14px',
                borderRadius: '9999px',
                border: 'none',
                cursor: 'pointer',
                fontSize: '13px',
                fontWeight: 600,
                transition: 'all 0.2s ease',
                backgroundColor: currentView === 'history' ? 'var(--accent)' : 'transparent',
                color: 'var(--color-cream)',
                boxShadow: currentView === 'history' ? '0 2px 8px rgba(158, 103, 82, 0.4)' : 'none'
              }}
            >
              <Clock size={14} />
              <span>History</span>
            </button>
          </div>
        )}

        {/* Right side: Controls & Profile */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          {/* Theme Toggle Button */}
          <button
            onClick={onToggleTheme}
            style={{
              width: '36px',
              height: '36px',
              borderRadius: '50%',
              backgroundColor: 'rgba(32, 33, 43, 0.45)',
              border: '1px solid rgba(254, 215, 165, 0.2)',
              color: 'var(--color-cream)',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              transition: 'transform 0.15s ease, background-color 0.2s ease'
            }}
            aria-label="Toggle theme"
            title={theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          >
            {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
          </button>

          {/* Logged in state */}
          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                backgroundColor: 'rgba(32, 33, 43, 0.45)',
                borderRadius: '9999px',
                padding: '3px 12px 3px 4px',
                border: '1px solid rgba(254, 215, 165, 0.15)'
              }}>
                <Avatar initials={avatarInitials} color={avatarColor} size={30} fontSize={11} />
                <span style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: 'var(--color-cream)',
                  maxWidth: '120px',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap'
                }}>
                  {displayName}
                </span>
              </div>

              <button
                onClick={logout}
                style={{
                  width: '34px',
                  height: '34px',
                  borderRadius: '50%',
                  background: 'none',
                  border: 'none',
                  color: 'var(--color-cream)',
                  opacity: 0.8,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  transition: 'opacity 0.2s ease'
                }}
                title="Log out"
                aria-label="Log out"
              >
                <LogOut size={16} />
              </button>
            </div>
          ) : (
            /* Logged out state */
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <button
                onClick={() => onNavigate('login')}
                style={{
                  padding: '7px 16px',
                  borderRadius: '9999px',
                  backgroundColor: currentView === 'login' ? 'rgba(158, 103, 82, 0.35)' : 'transparent',
                  color: 'var(--color-cream)',
                  border: '1px solid rgba(254, 215, 165, 0.2)',
                  fontSize: '13px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Log In
              </button>

              <button
                onClick={() => onNavigate('signup')}
                className="clay-btn"
                style={{
                  padding: '7px 18px',
                  borderRadius: '9999px',
                  fontSize: '13px',
                  boxShadow: '0 4px 12px rgba(158, 103, 82, 0.4)'
                }}
              >
                Sign Up
              </button>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
}
