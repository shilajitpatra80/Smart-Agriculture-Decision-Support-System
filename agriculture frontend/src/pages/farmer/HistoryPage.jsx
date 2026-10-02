import { useEffect, useState } from 'react';
import { api } from '../../api/client';

export default function HistoryPage() {
  const [entries, setEntries] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/api/recommend/history')
      .then(setEntries)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h1>Recommendation history</h1>
        <p>Every crop recommendation you've requested, most recent first.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="panel panel-flush">
        {entries === null && !error && <p className="muted" style={{ padding: 'var(--space-4)' }}>Loading…</p>}
        {entries && entries.length === 0 && <div className="empty-state">No recommendations requested yet.</div>}
        {entries && entries.map((e) => (
          <div className="ledger-row" key={e.id}>
            <div className="ledger-main">
              <div className="ledger-title">{e.recommendedCrop}</div>
              <div className="ledger-sub mono">{e.inputData}</div>
            </div>
            <div className="ledger-figure">{new Date(e.createdAt).toLocaleDateString()}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
