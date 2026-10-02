import { useEffect, useState } from 'react';
import { api } from '../../api/client';

const emptyForm = { id: null, cropId: '', name: '', symptoms: '', treatment: '' };

export default function DiseasesPage() {
  const [diseases, setDiseases] = useState(null);
  const [crops, setCrops] = useState([]);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');

  async function load() {
    try {
      const [diseaseData, cropData] = await Promise.all([
        api.get('/api/admin/diseases'),
        api.get('/api/admin/crops'),
      ]);
      setDiseases(diseaseData);
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

  function startEdit(d) {
    setForm({ id: d.id, cropId: d.cropId, name: d.name, symptoms: d.symptoms, treatment: d.treatment });
    setShowForm(true);
  }

  function startNew() {
    setForm({ ...emptyForm, cropId: crops[0]?.id || '' });
    setShowForm((v) => !v);
  }

  async function handleDelete(id) {
    if (!confirm('Delete this disease entry?')) return;
    try {
      await api.delete(`/api/admin/diseases/${id}`);
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
      symptoms: form.symptoms.trim(),
      treatment: form.treatment.trim(),
    };
    try {
      if (form.id) {
        await api.put(`/api/admin/diseases/${form.id}`, payload);
      } else {
        await api.post('/api/admin/diseases', payload);
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
          <h1>Diseases</h1>
          <p>Symptoms and treatment info shown to farmers per crop.</p>
        </div>
        <button className="btn-primary btn-small" onClick={startNew} disabled={crops.length === 0}>Add disease</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {crops.length === 0 && !error && <div className="alert alert-info">Add a crop first — diseases must be linked to one.</div>}

      {showForm && (
        <div className="panel">
          {formError && <div className="alert alert-error">{formError}</div>}
          <form onSubmit={handleSubmit}>
            <label>Crop</label>
            <select value={form.cropId} onChange={(e) => update('cropId', e.target.value)} required>
              {crops.map((c) => <option key={c.id} value={c.id}>{c.name} ({c.season})</option>)}
            </select>

            <label>Name</label>
            <input required value={form.name} onChange={(e) => update('name', e.target.value)} />

            <label>Symptoms</label>
            <textarea rows={2} required value={form.symptoms} onChange={(e) => update('symptoms', e.target.value)} />

            <label>Treatment</label>
            <textarea rows={2} required value={form.treatment} onChange={(e) => update('treatment', e.target.value)} />

            <button type="submit" className="btn-primary mt-4">Save disease</button>
          </form>
        </div>
      )}

      <div className="panel panel-flush">
        <table>
          <thead><tr><th>Crop</th><th>Name</th><th>Symptoms</th><th></th></tr></thead>
          <tbody>
            {diseases && diseases.map((d) => (
              <tr key={d.id}>
                <td>{d.cropName}</td>
                <td>{d.name}</td>
                <td>{d.symptoms.length > 60 ? d.symptoms.slice(0, 60) + '…' : d.symptoms}</td>
                <td>
                  <div className="cluster">
                    <button className="btn-outline btn-small" onClick={() => startEdit(d)}>Edit</button>
                    <button className="btn-danger-outline btn-small" onClick={() => handleDelete(d.id)}>Delete</button>
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
