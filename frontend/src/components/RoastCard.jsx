import React from 'react';

const CATEGORY_LABELS = {
  late_night_spending: 'LATE-NIGHT SPENDING',
  duplicate_purchases: 'DUPLICATE PURCHASES',
  subscription_creep: 'SUBSCRIPTION CREEP',
  impulse_category_spikes: 'IMPULSE SPENDING SPIKE',
  weekend_overspending: 'WEEKEND OVERSPENDING'
};

const SEVERITY_COLORS = {
  mild: {
    bg: 'rgba(115, 118, 106, 0.25)',
    border: 'var(--color-olive)',
    text: 'var(--color-cream)',
    label: 'Mild Ick'
  },
  medium: {
    bg: 'rgba(158, 103, 82, 0.3)',
    border: 'var(--color-terracotta)',
    text: 'var(--color-cream)',
    label: 'Medium Ick'
  },
  unserious: {
    bg: 'rgba(158, 103, 82, 0.5)',
    border: 'var(--color-terracotta)',
    text: '#FFFFFF',
    label: 'Wildly Unserious'
  }
};

export default function RoastCard({ category, roastText, severity = 'medium', emoji = '🔥' }) {
  const categoryLabel = CATEGORY_LABELS[category] || (category ? category.replace(/_/g, ' ').toUpperCase() : 'FINANCIAL ICK');
  const severityStyle = SEVERITY_COLORS[severity?.toLowerCase()] || SEVERITY_COLORS.medium;

  return (
    <div
      className="clay-card"
      style={{
        position: 'relative',
        overflow: 'hidden',
        padding: '28px 24px',
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        transition: 'transform 0.2s ease, box-shadow 0.2s ease'
      }}
    >
      {/* Subtle Blob Background Decoration */}
      <img
        src="/assets/roast-card-blob-decoration.png"
        alt=""
        style={{
          position: 'absolute',
          right: '-20px',
          bottom: '-20px',
          width: '140px',
          height: '140px',
          opacity: 0.12,
          pointerEvents: 'none',
          userSelect: 'none'
        }}
      />

      {/* Top Meta Row: Category Label & Severity Badge */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: '8px'
      }}>
        <span style={{
          fontSize: '11px',
          fontWeight: 700,
          letterSpacing: '0.08em',
          color: 'var(--text-secondary)',
          textTransform: 'uppercase'
        }}>
          {categoryLabel}
        </span>

        <span
          className="pill-badge"
          style={{
            backgroundColor: severityStyle.bg,
            border: `1px solid ${severityStyle.border}`,
            color: severityStyle.text,
            fontSize: '11px',
            padding: '3px 10px'
          }}
        >
          {severityStyle.label}
        </span>
      </div>

      {/* Main Roast Content */}
      <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
        <span
          style={{
            fontSize: '40px',
            lineHeight: 1,
            userSelect: 'none',
            filter: 'drop-shadow(0 4px 8px rgba(0, 0, 0, 0.25))'
          }}
          role="img"
          aria-label={category}
        >
          {emoji}
        </span>

        <p style={{
          fontFamily: 'var(--font-heading)',
          fontSize: '18px',
          fontWeight: 600,
          lineHeight: 1.45,
          color: 'var(--text-primary)',
          letterSpacing: '-0.01em'
        }}>
          "{roastText}"
        </p>
      </div>
    </div>
  );
}
