import { useState } from 'react';
import { api } from '../../api/client';

export default function WeatherPage() {
  const [location, setLocation] = useState('');
  const [weather, setWeather] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setWeather(null);
    setLoading(true);
    try {
      const data = await api.get(`/api/weather?location=${encodeURIComponent(location.trim())}`);
      setWeather(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Weather</h1>
        <p>Check current conditions for any location.</p>
      </div>

      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit} className="cluster">
          <input
            required
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="e.g. Nashik"
            style={{ flex: 1 }}
          />
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Checking…' : 'Check'}
          </button>
        </form>

        {weather && (
          <div className="mt-4">
            <div className="ledger-row"><div className="ledger-main">Temperature</div><div className="ledger-figure">{weather.temperature} °C</div></div>
            <div className="ledger-row"><div className="ledger-main">Rainfall (last hour)</div><div className="ledger-figure">{weather.rainfall} mm</div></div>
            <div className="ledger-row"><div className="ledger-main">Humidity</div><div className="ledger-figure">{weather.humidity}%</div></div>
            <p className="muted mt-4">Source: {weather.source === 'LIVE' ? 'just fetched' : 'cached from earlier today'}</p>
          </div>
        )}
      </div>
    </div>
  );
}
