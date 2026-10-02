import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../../api/client';

export default function FarmerHome() {
  const [tasks, setTasks] = useState(null); // null = loading
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/api/irrigation/today')
      .then(setTasks)
      .catch((err) => setError(err.message));
  }, []);

  const totalWater = tasks?.reduce((sum, t) => sum + Number(t.waterAmount), 0) ?? 0;
  const cropNames = tasks ? [...new Set(tasks.map((t) => t.cropName))].join(', ') : '';

  return (
    <div>
      <div className="page-header">
        <h1>Today</h1>
        <p>Your irrigation tasks at a glance.</p>
      </div>

      <div className="hero">
        <div className="eyebrow">Today's watering</div>
        {error && <div className="alert alert-error">{error}</div>}
        {!error && tasks === null && <p className="muted">Loading…</p>}
        {!error && tasks && tasks.length === 0 && (
          <>
            <h2 style={{ marginBottom: 0 }}>Nothing to water today</h2>
            <p className="muted">Check back tomorrow, or generate a new schedule.</p>
          </>
        )}
        {!error && tasks && tasks.length > 0 && (
          <>
            <h2 style={{ marginBottom: 0 }}>Water the {cropNames} today</h2>
            <p className="muted">
              {tasks.length} task{tasks.length > 1 ? 's' : ''} pending — <span className="mono">{totalWater.toFixed(1)}</span> total
            </p>
          </>
        )}
      </div>

      <div className="panel">
        <h2>Quick links</h2>
        <div className="cluster">
          <Link className="btn-outline" to="/farmer/recommend">Recommend a crop</Link>
          <Link className="btn-outline" to="/farmer/schedule">View full schedule</Link>
          <Link className="btn-outline" to="/farmer/profile">Edit my profile</Link>
          <Link className="btn-outline" to="/farmer/weather">Check weather</Link>
        </div>
      </div>
    </div>
  );
}
