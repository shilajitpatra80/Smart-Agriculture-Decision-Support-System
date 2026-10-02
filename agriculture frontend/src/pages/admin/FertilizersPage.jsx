import { useEffect, useState } from 'react';
import { api } from '../../api/client';

const emptyForm = { id: null, cropId: '', name: '', quantityPerAcre: '', stageOfApplication: '' };

export default function FertilizersPage() {
  const [fertilizers, setFertilizers] = useState(null);
  const [crops, setCrops] = useState([]);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');

  async function load() {
    try {
      const [fertilizerData, cropData] = await Promise.all([
        api.get('/api/admin/fertilizers'),
        api.get('/api/admin/crops'),
      ]);
      setFertilizers(fertilizerData);
      setCrops(cropData);
      if (!form.cropId && cropData.length > 0) {
        setForm((prev) => ({ ...prev, cropId: cropData[0].id }));
      }
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function startEdit(f) {
    setForm({ id: f.id, cropId: f.cropId, name: f.name, quantityPerAcre: f.quantityPerAcre, stageOfApplication: f.stageOfApplication });
    setShowForm(true);
  }

  function startNew() {
    setForm({ ...emptyForm, cropId: crops[0]?.id || '' });
    setShowForm((v) => !v);
  }

  async function handleDelete(id) {
    if (!confirm('Delete this fertilizer entry?')) return;
    try {
      await api.delete(`/api/admin/fertilizers/${id}`);
      load();
    } catch (err) {
      alert(err.message);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError('');
    const payload = {
      cropId: Number(form.cropId),
      name: form.name.trim(),
      quantityPerAcre: Number(form.quantityPerAcre),
      stageOfApplication: form.stageOfApplication.trim(),
    };
    try {
      if (form.id) {
        await api.put(`/api/admin/fertilizers/${form.id}`, payload);
      } else {
        await api.post('/api/admin/fertilizers', payload);
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
          <h1>Fertilizers</h1>
          <p>Recommended fertilizers per crop and growth stage.</p>
        </div>
        <button className="btn-primary btn-small" onClick={startNew} disabled={crops.length === 0}>Add fertilizer</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {crops.length === 0 && !error && <div className="alert alert-info">Add a crop first — fertilizers must be linked to one.</div>}

      {showForm && (
        <div className="panel">
          {formError && <div className="alert alert-error">{formError}</div>}
          <form onSubmit={handleSubmit}>
            <label>Crop</label>
            <select value={form.cropId} onChange={(e) => update('cropId', e.target.value)} required>
              {crops.map((c) => <option key={c.id} value={c.id}>{c.name} ({c.season})</option>)}
            </select>

            <div className="field-row">
              <div><label>Name</label><input required value={form.name} onChange={(e) => update('name', e.target.value)} /></div>
              <div><label>Quantity per acre (kg)</label><input type="number" step="0.1" required value={form.quantityPerAcre} onChange={(e) => update('quantityPerAcre', e.target.value)} /></div>
            </div>

            <label>Stage of application</label>
            <input required value={form.stageOfApplication} onChange={(e) => update('stageOfApplication', e.target.value)} placeholder="e.g. Sowing" />

            <button type="submit" className="btn-primary mt-4">Save fertilizer</button>
          </form>
        </div>
      )}

      <div className="panel panel-flush">
        <table>
          <thead><tr><th>Crop</th><th>Name</th><th>Qty/acre</th><th>Stage</th><th></th></tr></thead>
          <tbody>
            {fertilizers && fertilizers.map((f) => (
              <tr key={f.id}>
                <td>{f.cropName}</td>
                <td>{f.name}</td>
                <td className="num">{f.quantityPerAcre} kg</td>
                <td>{f.stageOfApplication}</td>
                <td>
                  <div className="cluster">
                    <button className="btn-outline btn-small" onClick={() => startEdit(f)}>Edit</button>
                    <button className="btn-danger-outline btn-small" onClick={() => handleDelete(f.id)}>Delete</button>
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
