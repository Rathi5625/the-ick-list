import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import RoastCard from '../components/RoastCard';
import ErrorBanner from '../components/ErrorBanner';
import { Clock, Calendar, Flame, RefreshCw, Upload, Sparkles } from 'lucide-react';

export default function RoastHistoryPage({ onNavigateToUpload }) {
  const { token } = useAuth();
  const [roasts, setRoasts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchHistory = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/roasts/history', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.error || 'Failed to load roast history');
      }
      setRoasts(data.roasts || []);
    } catch (err) {
      setError(err.message || 'Could not retrieve roast history');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, [token]);

  // Group roasts by uploadBatchId, maintaining newest-first batch order
  const groupRoastsByBatch = (list) => {
    const batchesMap = new Map();

    list.forEach((roast) => {
      const batchId = roast.uploadBatchId || 'legacy-batch';
      if (!batchesMap.has(batchId)) {
        batchesMap.set(batchId, {
          batchId,
          createdAt: roast.createdAt,
          items: []
        });
      }
      const batch = batchesMap.get(batchId);
      // Keep earliest or latest timestamp associated with the batch
      if (!batch.createdAt && roast.createdAt) {
        batch.createdAt = roast.createdAt;
      }
      batch.items.push(roast);
    });

    // Convert map to array and sort batches newest first
    const sortedBatches = Array.from(batchesMap.values());
    sortedBatches.sort((a, b) => {
      if (!a.createdAt || !b.createdAt) return 0;
      return new Date(b.createdAt) - new Date(a.createdAt);
    });

    return sortedBatches;
  };

  const formatDate = (isoString) => {
    if (!isoString) return 'Statement Upload';
    try {
      const d = new Date(isoString);
      if (isNaN(d.getTime())) return 'Statement Upload';
      return d.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: 'numeric',
        minute: '2-digit'
      });
    } catch {
      return 'Statement Upload';
    }
  };

  const batches = groupRoastsByBatch(roasts);

  return (
    <div style={{ maxWidth: '760px', width: '100%', margin: '0 auto', padding: '16px 0' }}>
      {/* Header */}
      <div style={{ textAlign: 'center', marginBottom: '36px' }}>
        <div className="pill-badge" style={{ marginBottom: '14px', backgroundColor: 'var(--color-teal)' }}>
          <Clock size={14} /> Audit Trail
        </div>
        <h1 style={{ fontSize: '36px', marginBottom: '12px' }}>
          Roast History
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '15px', maxWidth: '520px', margin: '0 auto' }}>
          A timeline of your worst financial moments. Every bad decision you've ever uploaded, right here.
        </p>
      </div>

      <ErrorBanner message={error} onDismiss={() => setError(null)} />

      {/* Loading State */}
      {isLoading && (
        <div className="clay-card" style={{ padding: '48px 24px', textAlign: 'center' }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            backgroundColor: 'rgba(158, 103, 82, 0.2)',
            marginBottom: '16px'
          }}>
            <RefreshCw size={24} color="var(--accent)" style={{ animation: 'spin 1.2s linear infinite' }} />
          </div>
          <h3 style={{ fontSize: '18px', marginBottom: '6px' }}>Retrieving your past roasts...</h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Querying DynamoDB for your past audits</p>
        </div>
      )}

      {/* Empty State */}
      {!isLoading && roasts.length === 0 && (
        <div
          className="clay-card"
          style={{
            padding: '52px 28px',
            textAlign: 'center',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}
        >
          <img
            src="/assets/history-empty-state-folder-clock.png"
            alt="No roast history"
            style={{
              width: '88px',
              height: '88px',
              objectFit: 'contain',
              marginBottom: '20px',
              filter: 'drop-shadow(0 6px 12px rgba(0,0,0,0.25))'
            }}
          />
          <h2 style={{ fontSize: '24px', marginBottom: '10px' }}>
            No Roast History Yet!
          </h2>
          <p style={{
            color: 'var(--text-secondary)',
            fontSize: '15px',
            maxWidth: '440px',
            lineHeight: 1.5,
            marginBottom: '28px'
          }}>
            Your financial record is currently spotless... or you just haven't uploaded a statement yet. Upload a CSV to get your first roast!
          </p>
          <button
            onClick={onNavigateToUpload}
            className="clay-btn"
            style={{ padding: '12px 28px', fontSize: '15px', gap: '8px' }}
          >
            <Upload size={16} />
            <span>Upload Your First Statement</span>
          </button>
        </div>
      )}

      {/* Grouped Batches List */}
      {!isLoading && batches.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '40px' }}>
          {batches.map((batch, batchIndex) => (
            <div key={batch.batchId} style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
              {/* Batch Header */}
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                flexWrap: 'wrap',
                gap: '10px',
                padding: '10px 16px',
                backgroundColor: 'rgba(45, 67, 84, 0.35)',
                borderRadius: '12px',
                border: '1px solid var(--border-color)'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Calendar size={16} color="var(--accent)" />
                  <span style={{
                    fontFamily: 'var(--font-heading)',
                    fontSize: '14px',
                    fontWeight: 700,
                    color: 'var(--text-primary)'
                  }}>
                    {formatDate(batch.createdAt)}
                  </span>
                  {batchIndex === 0 && (
                    <span className="pill-badge" style={{ backgroundColor: 'var(--accent)', fontSize: '10px', padding: '2px 8px' }}>
                      Latest
                    </span>
                  )}
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '12px', color: 'var(--text-secondary)', fontWeight: 600 }}>
                    {batch.items.length} {batch.items.length === 1 ? 'Roast' : 'Roasts'}
                  </span>
                  <span style={{
                    fontSize: '11px',
                    color: 'var(--text-secondary)',
                    fontFamily: 'monospace',
                    backgroundColor: 'rgba(32, 33, 43, 0.4)',
                    padding: '2px 6px',
                    borderRadius: '4px'
                  }}>
                    Batch #{batch.batchId.slice(0, 8)}
                  </span>
                </div>
              </div>

              {/* Batch Cards */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {batch.items.map((roast, index) => (
                  <RoastCard
                    key={roast.roastId || `${batch.batchId}-${index}`}
                    category={roast.category}
                    roastText={roast.roastText}
                    severity={roast.severity}
                    emoji={roast.emoji}
                  />
                ))}
              </div>
            </div>
          ))}

          {/* Action Footer */}
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            paddingTop: '24px',
            borderTop: '1px solid var(--border-color)',
            marginTop: '12px'
          }}>
            <button
              onClick={onNavigateToUpload}
              className="clay-btn"
              style={{ padding: '12px 26px', fontSize: '14px', gap: '8px' }}
            >
              <Upload size={16} />
              <span>Upload Another Statement</span>
            </button>
          </div>
        </div>
      )}

      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
