import React from 'react';

export default function Avatar({ initials = 'U', color = '#9E6752', size = 36, fontSize = 13 }) {
  return (
    <div
      style={{
        width: `${size}px`,
        height: `${size}px`,
        borderRadius: '50%',
        backgroundColor: color,
        color: '#FED7A5',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontFamily: 'var(--font-heading)',
        fontWeight: 700,
        fontSize: `${fontSize}px`,
        letterSpacing: '0.04em',
        userSelect: 'none',
        border: '2px solid rgba(254, 215, 165, 0.25)',
        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.3)'
      }}
      title={`User (${initials})`}
    >
      {initials}
    </div>
  );
}
