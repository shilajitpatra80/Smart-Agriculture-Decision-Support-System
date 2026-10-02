import { useEffect, useState } from 'react';
import { api } from '../../api/client';

export default function ProfilePage() {
  const [form, setForm] = useState({ location: '', landSize: '', soilType: '', waterAvailability: '' });
  const [message, setMessage] = useState(null); // { type, text }
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    api.get('/api/farmer/profile')
      .then((profile) => setForm({
        location: profile.location || '',
        landSize: profile.landSize ?? '',
        soilType: profile.soilType || '',
        waterAvailability: profile.waterAvailability ?? '',
      }))
      .catch(() => setMessage({ type: 'info', text: 'No profile yet — fill this in to get started.' }));
  }, []);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage(null);
    setSaving(true);
    try {
      await api.post('/api/farmer/profile', {
        location: form.location.trim(),
        landSize: Number(form.landSize),
        soilType: form.soilType.trim(),
        waterAvailability: Number(form.waterAvailability),
      });
      setMessage({ type: 'info', text: 'Profile saved.' });
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>My profile</h1>
        <p>Used as the default for crop recommendations and irrigation scheduling.</p>
      </div>

      <div className="panel">
        {message && <div className={`alert alert-${message.type}`}>{message.text}</div>}
        <form onSubmit={handleSubmit}>
          <label htmlFor="location">Location</label>
          <input id="location" required value={form.location} onChange={(e) => update('location', e.target.value)} placeholder="e.g. Nashik, Maharashtra" />

          <div className="field-row">
            <div>
              <label htmlFor="landSize">Land size (acres)</label>
              <input id="landSize" type="number" step="0.1" min="0" required value={form.landSize} onChange={(e) => update('landSize', e.target.value)} />
            </div>
            <div>
              <label htmlFor="soilType">Soil type</label>
              <input id="soilType" required value={form.soilType} onChange={(e) => update('soilType', e.target.value)} placeholder="e.g. Loamy" />
            </div>
          </div>

          <label htmlFor="waterAvailability">Water availability</label>
          <input id="waterAvailability" type="number" step="0.1" min="0" required value={form.waterAvailability} onChange={(e) => update('waterAvailability', e.target.value)} />

          <button type="submit" className="btn-primary mt-4" disabled={saving}>
            {saving ? 'Saving…' : 'Save profile'}
          </button>
        </form>
      </div>
    </div>
  );
}
