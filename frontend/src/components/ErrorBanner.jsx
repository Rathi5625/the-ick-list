import React from 'react';
import { AlertCircle } from 'lucide-react';

export default function ErrorBanner({ message, onDismiss }) {
  if (!message) return null;

  return (
    <div style={{
      backgroundColor: 'rgba(158, 103, 82, 0.15)',
      border: '1px solid var(--accent)',
      borderRadius: '12px',
      padding: '12px 16px',
      display: 'flex',
      alignItems: 'center',
      gap: '12px',
      color: 'var(--text-primary)',
      marginBottom: '16px',
      boxShadow: 'var(--clay-card-shadow)'
    }}>
      <AlertCircle size={20} color="var(--accent)" style={{ flexShrink: 0 }} />
      <span style={{ fontSize: '14px', flex: 1 }}>{message}</span>
      {onDismiss && (
        <button
          onClick={onDismiss}
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--text-secondary)',
            cursor: 'pointer',
            fontSize: '16px',
            padding: '4px'
          }}
          aria-label="Dismiss error"
        >
          ×
        </button>
      )}
    </div>
  );
}
