import { useEffect, useState } from 'react';
import { api } from '../../api/client';

export default function FarmersPage() {
  const [farmers, setFarmers] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/api/admin/farmers')
      .then(setFarmers)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h1>Registered farmers</h1>
        <p>Read-only — farmers manage their own profile.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="panel panel-flush">
        <table>
          <thead><tr><th>Name</th><th>Location</th><th>Soil</th><th>Land size</th><th>Water avail.</th></tr></thead>
          <tbody>
            {farmers && farmers.length === 0 && (
              <tr><td colSpan={5} className="muted">No farmers have completed their profile yet.</td></tr>
            )}
            {farmers && farmers.map((f) => (
              <tr key={f.id}>
                <td>{f.name}</td>
                <td>{f.location}</td>
                <td>{f.soilType}</td>
                <td className="num">{f.landSize} acres</td>
                <td className="num">{f.waterAvailability}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
