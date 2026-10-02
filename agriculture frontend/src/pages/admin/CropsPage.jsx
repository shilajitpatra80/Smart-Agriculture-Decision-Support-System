import { useEffect, useState } from 'react';
import { api } from '../../api/client';

const emptyForm = { id: null, name: '', season: 'KHARIF', soilType: '', idealTempRange: '', minWater: '', maxWater: '' };

export default function CropsPage() {
  const [crops, setCrops] = useState(null);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');

  async function load() {
    try {
      setCrops(await api.get('/api/admin/crops'));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function startEdit(crop) {
    setForm({
      id: crop.id, name: crop.name, season: crop.season, soilType: crop.soilType,
      idealTempRange: crop.idealTempRange || '', minWater: crop.minWater, maxWater: crop.maxWater,
    });
    setShowForm(true);
  }

  function startNew() {
    setForm(emptyForm);
    setShowForm((v) => !v);
  }

  async function handleDelete(id) {
    if (!confirm('Delete this crop? Its fertilizers and diseases will be deleted too.')) return;
    try {
      await api.delete(`/api/admin/crops/${id}`);
      load();
    } catch (err) {
      alert(err.message);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError('');
    const payload = {
      name: form.name.trim(),
      season: form.season,
      soilType: form.soilType.trim(),
      idealTempRange: form.idealTempRange.trim() || null,
      minWater: Number(form.minWater),
      maxWater: Number(form.maxWater),
    };
    try {
      if (form.id) {
        await api.put(`/api/admin/crops/${form.id}`, payload);
      } else {
        await api.post('/api/admin/crops', payload);
      }
      setForm(emptyForm);
      setShowForm(false);
      load();
    } catch (err) {
      setFormError(err.message);
    }
  }

  return (
    <div>
      <div className="page-header spread">
        <div>
          <h1>Crops</h1>
          <p>Master data used by the recommendation engine.</p>
        </div>
        <button className="btn-primary btn-small" onClick={startNew}>Add crop</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {showForm && (
        <div className="panel">
          {formError && <div className="alert alert-error">{formError}</div>}
          <form onSubmit={handleSubmit}>
            <div className="field-row">
              <div><label>Name</label><input required value={form.name} onChange={(e) => update('name', e.target.value)} /></div>
              <div>
                <label>Season</label>
                <select value={form.season} onChange={(e) => update('season', e.target.value)}>
                  <option value="KHARIF">Kharif</option>
                  <option value="RABI">Rabi</option>
                  <option value="ZAID">Zaid</option>
                  <option value="ALL_SEASON">All season</option>
                </select>
              </div>
            </div>
            <div className="field-row">
              <div><label>Soil type</label><input required value={form.soilType} onChange={(e) => update('soilType', e.target.value)} /></div>
              <div><label>Ideal temp range</label><input value={form.idealTempRange} onChange={(e) => update('idealTempRange', e.target.value)} placeholder="e.g. 20-30°C" /></div>
            </div>
            <div className="field-row">
              <div><label>Min water</label><input type="number" step="0.1" required value={form.minWater} onChange={(e) => update('minWater', e.target.value)} /></div>
              <div><label>Max water</label><input type="number" step="0.1" required value={form.maxWater} onChange={(e) => update('maxWater', e.target.value)} /></div>
            </div>
            <button type="submit" className="btn-primary mt-4">Save crop</button>
          </form>
        </div>
      )}

      <div className="panel panel-flush">
        <table>
          <thead><tr><th>Name</th><th>Season</th><th>Soil</th><th>Water range</th><th></th></tr></thead>
          <tbody>
            {crops && crops.map((c) => (
              <tr key={c.id}>
                <td>{c.name}</td>
                <td>{c.season}</td>
                <td>{c.soilType}</td>
                <td className="num">{c.minWater}–{c.maxWater}</td>
                <td>
                  <div className="cluster">
                    <button className="btn-outline btn-small" onClick={() => startEdit(c)}>Edit</button>
                    <button className="btn-danger-outline btn-small" onClick={() => handleDelete(c.id)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
