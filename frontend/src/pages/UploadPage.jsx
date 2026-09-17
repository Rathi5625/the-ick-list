import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import ErrorBanner from '../components/ErrorBanner';
import { UploadCloud, FileSpreadsheet, Loader2, Download, Flame } from 'lucide-react';

export default function UploadPage({ onRoastsGenerated }) {
  const { token } = useAuth();
  const [file, setFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [loadingStep, setLoadingStep] = useState(''); // 'uploading' or 'generating'
  const [error, setError] = useState(null);

  const handleFileChange = (e) => {
    setError(null);
    if (e.target.files && e.target.files[0]) {
      const selected = e.target.files[0];
      if (!selected.name.toLowerCase().endsWith('.csv')) {
        setError('Please select a valid CSV file (.csv)');
        setFile(null);
        return;
      }
      setFile(selected);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setError(null);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const dropped = e.dataTransfer.files[0];
      if (!dropped.name.toLowerCase().endsWith('.csv')) {
        setError('Please drop a valid CSV file (.csv)');
        return;
      }
      setFile(dropped);
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  const handleUploadAndRoast = async () => {
    if (!file) {
      setError('Please choose a CSV file first');
      return;
    }

    setIsLoading(true);
    setError(null);
    setLoadingStep('Uploading & validating statement...');

    const formData = new FormData();
    formData.append('file', file);

    try {
      // Step 1: Upload CSV
      const uploadRes = await fetch('/api/transactions/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      const uploadData = await uploadRes.json();

      if (!uploadRes.ok) {
        throw new Error(uploadData.error || 'Failed to upload transactions');
      }

      // Step 2: Generate Roasts
      setLoadingStep('Calling out your bad financial habits...');
      const roastRes = await fetch('/api/roasts/generate', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ uploadBatchId: uploadData.uploadBatchId })
      });

      const roastData = await roastRes.json();

      if (!roastRes.ok) {
        throw new Error(roastData.error || 'Failed to generate roasts');
      }

      if (onRoastsGenerated) {
        onRoastsGenerated(roastData.roasts);
      }
    } catch (err) {
      setError(err.message || 'Error processing statement');
    } finally {
      setIsLoading(false);
      setLoadingStep('');
    }
  };

  return (
    <div style={{ maxWidth: '640px', width: '100%', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '32px' }}>
        <h1 style={{ fontSize: '32px', marginBottom: '8px' }}>
          Upload Your Statement
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '15px' }}>
          Upload a sample transaction CSV (columns: <code>Date</code>, <code>Amount</code>, <code>Merchant</code>, <code>Category</code>)
        </p>
      </div>

      <ErrorBanner message={error} onDismiss={() => setError(null)} />

      {/* File Input Card */}
      <div className="clay-card" style={{ padding: '36px 28px' }}>
        {/* Dropzone */}
        <div
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          style={{
            border: '2px dashed var(--border-color)',
            borderRadius: '14px',
            padding: '36px 20px',
            textAlign: 'center',
            backgroundColor: file ? 'rgba(158, 103, 82, 0.08)' : 'rgba(32, 33, 43, 0.15)',
            cursor: 'pointer',
            marginBottom: '24px',
            transition: 'border-color 0.2s ease, background-color 0.2s ease'
          }}
          onClick={() => document.getElementById('csv-file-input').click()}
        >
          <input
            id="csv-file-input"
            type="file"
            accept=".csv"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />

          <img
            src="/assets/upload-empty-state-receipt-stack.png"
            alt="Upload receipt stack"
            style={{
              width: '64px',
              height: '64px',
              objectFit: 'contain',
              margin: '0 auto 16px',
              filter: 'drop-shadow(0 4px 6px rgba(0,0,0,0.2))'
            }}
          />

          {file ? (
            <div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '6px' }}>
                <FileSpreadsheet size={20} color="var(--accent)" />
                <strong style={{ fontSize: '15px' }}>{file.name}</strong>
              </div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '13px' }}>
                {(file.size / 1024).toFixed(1)} KB • Click to choose a different file
              </div>
            </div>
          ) : (
            <div>
              <div style={{ fontSize: '16px', fontWeight: 600, marginBottom: '6px' }}>
                Click to browse or drag & drop CSV file
              </div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '13px' }}>
                Must include Date, Amount, Merchant, Category
              </div>
            </div>
          )}
        </div>

        {/* Action Button */}
        <button
          onClick={handleUploadAndRoast}
          disabled={!file || isLoading}
          className="clay-btn"
          style={{ width: '100%', padding: '14px', fontSize: '16px', marginBottom: '16px', gap: '10px' }}
        >
          {isLoading ? (
            <>
              <Loader2 size={18} style={{ animation: 'spin 1s linear infinite' }} />
              <span>{loadingStep || 'Processing...'}</span>
            </>
          ) : (
            <>
              <Flame size={18} />
              <span>Roast Me!</span>
            </>
          )}
        </button>

        {/* Sample CSV helper download */}
        <div style={{ textAlign: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px' }}>
          <a
            href="/sample-transactions.csv"
            download="sample-transactions.csv"
            style={{
              color: 'var(--accent)',
              fontSize: '13px',
              textDecoration: 'none',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
              fontWeight: 600
            }}
          >
            <Download size={14} />
            <span>Download sample-transactions.csv to test</span>
          </a>
        </div>
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
