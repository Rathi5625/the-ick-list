import React from 'react';
import RoastCard from '../components/RoastCard';
import { Flame, RefreshCw, Clock } from 'lucide-react';

export default function RoastResultsPage({ roasts = [], onUploadAgain, onNavigateToHistory }) {
  return (
    <div style={{ maxWidth: '760px', width: '100%', margin: '0 auto', padding: '16px 0' }}>
      {/* Header */}
      <div style={{ textAlign: 'center', marginBottom: '36px' }}>
        <div className="pill-badge" style={{ marginBottom: '14px', backgroundColor: 'var(--accent)' }}>
          <Flame size={14} /> Official Verdict
        </div>
        <h1 style={{ fontSize: '36px', marginBottom: '12px' }}>
          Your Financial Ick List
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '16px', maxWidth: '540px', margin: '0 auto' }}>
          Here is what your bank statement says about your life choices. Don't shoot the messenger.
        </p>
      </div>

      {/* Cards List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', marginBottom: '40px' }}>
        {roasts.map((roast, index) => (
          <RoastCard
            key={roast.roastId || index}
            category={roast.category}
            roastText={roast.roastText}
            severity={roast.severity}
            emoji={roast.emoji}
          />
        ))}
      </div>

      {/* Action Footer */}
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        flexWrap: 'wrap',
        gap: '14px',
        paddingTop: '20px',
        borderTop: '1px solid var(--border-color)'
      }}>
        <button
          onClick={onUploadAgain}
          className="clay-btn"
          style={{ padding: '12px 24px', fontSize: '14px' }}
        >
          <RefreshCw size={16} />
          <span>Upload Another Statement</span>
        </button>

        <button
          onClick={onNavigateToHistory}
          style={{
            background: 'rgba(32, 33, 43, 0.35)',
            border: '1px solid var(--border-color)',
            color: 'var(--text-primary)',
            padding: '12px 20px',
            borderRadius: '10px',
            cursor: 'pointer',
            fontWeight: 600,
            fontSize: '14px',
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            transition: 'background-color 0.2s ease'
          }}
        >
          <Clock size={16} />
          <span>View Roast History</span>
        </button>
      </div>
    </div>
  );
}
