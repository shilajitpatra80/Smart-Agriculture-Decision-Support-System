import { useState } from 'react';
import { api } from '../../api/client';

export default function RecommendPage() {
  const [season, setSeason] = useState('KHARIF');
  const [soilType, setSoilType] = useState('');
  const [availableWater, setAvailableWater] = useState('');
  const [results, setResults] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);

    const payload = { season };
    if (soilType.trim()) payload.soilType = soilType.trim();
    if (availableWater) payload.availableWater = Number(availableWater);

    try {
      const data = await api.post('/api/recommend/crop', payload);
      setResults(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Recommend a crop</h1>
        <p>Get a ranked list based on your soil, water, and the season.</p>
      </div>

      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <label htmlFor="season">Season</label>
          <select id="season" value={season} onChange={(e) => setSeason(e.target.value)}>
            <option value="KHARIF">Kharif</option>
            <option value="RABI">Rabi</option>
            <option value="ZAID">Zaid</option>
          </select>

          <div className="field-row">
            <div>
              <label htmlFor="soilType">Soil type <span className="muted">(optional — uses your profile if blank)</span></label>
              <input id="soilType" value={soilType} onChange={(e) => setSoilType(e.target.value)} placeholder="e.g. Loamy" />
            </div>
            <div>
              <label htmlFor="availableWater">Available water <span className="muted">(optional)</span></label>
              <input id="availableWater" type="number" step="0.1" min="0" value={availableWater} onChange={(e) => setAvailableWater(e.target.value)} placeholder="e.g. 80" />
            </div>
          </div>

          <button type="submit" className="btn-primary mt-4" disabled={loading}>
            {loading ? 'Thinking…' : 'Get recommendations'}
          </button>
        </form>
      </div>

      {results && results.length === 0 && (
        <div className="empty-state">No crops matched. Try a different season or water amount.</div>
      )}

      {results && results.map((crop, i) => (
        <RecommendationCard key={crop.cropId} crop={crop} rank={i + 1} />
      ))}
    </div>
  );
}

function RecommendationCard({ crop, rank }) {
  const [showScheduleForm, setShowScheduleForm] = useState(false);
  const [startDate, setStartDate] = useState('');
  const [intervalDays, setIntervalDays] = useState('');
  const [durationDays, setDurationDays] = useState('');
  const [scheduleMsg, setScheduleMsg] = useState(null); // { type, text }

  const [cropInfo, setCropInfo] = useState(null); // null = not loaded, 'loading', or { diseases, fertilizers }
  const [infoError, setInfoError] = useState('');

  async function handleGenerateSchedule() {
    setScheduleMsg(null);
    const payload = { cropId: crop.cropId };
    if (startDate) payload.startDate = startDate;
    if (intervalDays) payload.intervalDays = Number(intervalDays);
    if (durationDays) payload.durationDays = Number(durationDays);

    try {
      const entries = await api.post('/api/irrigation/schedule', payload);
      setScheduleMsg({ type: 'info', text: `Schedule created: ${entries.length} watering sessions. See "Irrigation schedule".` });
    } catch (err) {
      setScheduleMsg({ type: 'error', text: err.message });
    }
  }

  async function handleLoadInfo() {
    setCropInfo('loading');
    setInfoError('');
    try {
      const [diseases, fertilizers] = await Promise.all([
        api.get(`/api/disease/${crop.cropId}`),
        api.get(`/api/fertilizer/${crop.cropId}`),
      ]);
      setCropInfo({ diseases, fertilizers });
    } catch (err) {
      setInfoError(err.message);
      setCropInfo(null);
    }
  }

  return (
    <div className="panel">
      <div className="spread">
        <h3>{rank}. {crop.cropName}</h3>
        <span className="mono">{crop.score}/100</span>
      </div>
      <div className="score-bar-track"><div className="score-bar-fill" style={{ width: `${crop.score}%` }} /></div>
      <ul className="reason-list">
        {crop.matchReasons.map((r, idx) => <li key={idx}>{r}</li>)}
      </ul>

      <div className="cluster mt-4">
        <button className="btn-outline btn-small" onClick={() => setShowScheduleForm((v) => !v)}>Generate irrigation schedule</button>
        <button className="btn-outline btn-small" onClick={handleLoadInfo}>View disease &amp; fertilizer info</button>
      </div>

      {showScheduleForm && (
        <div className="mt-4" style={{ borderTop: '1px solid var(--color-border-soft)', paddingTop: 'var(--space-3)' }}>
          <div className="field-row">
            <div><label>Start date</label><input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} /></div>
            <div><label>Interval (days)</label><input type="number" min="1" placeholder="4" value={intervalDays} onChange={(e) => setIntervalDays(e.target.value)} /></div>
            <div><label>Duration (days)</label><input type="number" min="1" placeholder="30" value={durationDays} onChange={(e) => setDurationDays(e.target.value)} /></div>
          </div>
          <button className="btn-primary btn-small mt-4" onClick={handleGenerateSchedule}>Confirm schedule</button>
          {scheduleMsg && <div className={`alert alert-${scheduleMsg.type} mt-4`}>{scheduleMsg.text}</div>}
        </div>
      )}

      {infoError && <div className="alert alert-error mt-4">{infoError}</div>}
      {cropInfo === 'loading' && <p className="muted mt-4">Loading…</p>}
      {cropInfo && cropInfo !== 'loading' && (
        <div className="mt-4" style={{ borderTop: '1px solid var(--color-border-soft)', paddingTop: 'var(--space-3)' }}>
          <div className="muted" style={{ fontSize: '0.78rem', marginBottom: 'var(--space-1)' }}>Diseases</div>
          {cropInfo.diseases.length === 0 && <p className="muted">No disease info recorded for this crop yet.</p>}
          {cropInfo.diseases.map((d) => (
            <div className="ledger-row" key={d.id}>
              <div className="ledger-main">
                <div className="ledger-title">{d.name}</div>
                <div className="ledger-sub">{d.symptoms} — Treatment: {d.treatment}</div>
              </div>
            </div>
          ))}

          <div className="muted mt-4" style={{ fontSize: '0.78rem', marginBottom: 'var(--space-1)' }}>Fertilizers</div>
          {cropInfo.fertilizers.length === 0 && <p className="muted">No fertilizer info recorded for this crop yet.</p>}
          {cropInfo.fertilizers.map((f) => (
            <div className="ledger-row" key={f.id}>
              <div className="ledger-main">
                <div className="ledger-title">{f.name}</div>
                <div className="ledger-sub">{f.stageOfApplication}</div>
              </div>
              <div className="ledger-figure">{f.quantityPerAcre} / acre</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
