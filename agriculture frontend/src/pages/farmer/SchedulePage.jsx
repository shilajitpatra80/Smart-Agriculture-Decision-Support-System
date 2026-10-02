import { useEffect, useState } from 'react';
import { api } from '../../api/client';

export default function SchedulePage() {
  const [entries, setEntries] = useState(null);
  const [error, setError] = useState('');

  async function load() {
    try {
      const data = await api.get('/api/irrigation/my-schedule');
      data.sort((a, b) => a.scheduleDate.localeCompare(b.scheduleDate));
      setEntries(data);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  async function updateStatus(id, status) {
    try {
      await api.patch(`/api/irrigation/${id}/status`, { status });
      load();
    } catch (err) {
      alert(err.message);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Irrigation schedule</h1>
        <p>Generated from the "Recommend a crop" page.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="panel panel-flush">
        {entries === null && !error && <p className="muted" style={{ padding: 'var(--space-4)' }}>Loading…</p>}
        {entries && entries.length === 0 && (
          <div className="empty-state">No schedule yet. Generate one from the "Recommend a crop" page.</div>
        )}
        {entries && entries.map((e) => (
          <div className="ledger-row" key={e.id}>
            <div className="ledger-main">
              <div className="ledger-title">{e.cropName}</div>
              <div className="ledger-sub">{e.scheduleDate}</div>
            </div>
            <div className="ledger-figure">{e.waterAmount}</div>
            <span className={`badge badge-${e.status.toLowerCase()}`}>{e.status}</span>
            {e.status === 'PENDING' && (
              <div className="ledger-actions">
                <button className="btn-outline btn-small" onClick={() => updateStatus(e.id, 'COMPLETED')}>Done</button>
                <button className="btn-outline btn-small" onClick={() => updateStatus(e.id, 'SKIPPED')}>Skip</button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
