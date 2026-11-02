import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import api from '../../api/client'

const initialForm = {
  nom: '', villeDepart: 'Dakar', departLe: '', capacite: 55,
  prixDepartKaolack: '', lienWaveDepartKaolack: '', prixDepartDestination: '',
  lienWaveDepartDestination: '', prixKaolackDestination: '', lienWaveKaolackDestination: '',
}

export default function AdminCaravanForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const editing = Boolean(id)
  const [form, setForm] = useState(initialForm)
  const [loading, setLoading] = useState(editing)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const villeArrivee = form.villeDepart === 'Dakar' ? 'Ziguinchor' : 'Dakar'

  useEffect(() => {
    if (!editing) return
    api.get(`/api/admin/caravanes/${id}`).then(({ data }) => setForm((current) => ({
      ...current,
      nom: data.nom,
      departLe: data.departLe?.slice(0, 16) ?? '',
    }))).catch((requestError) => setError(requestError.response?.data?.message ?? 'Impossible de charger cette caravane.'))
      .finally(() => setLoading(false))
  }, [editing, id])

  const setValue = (key) => (event) => setForm((current) => ({ ...current, [key]: event.target.value }))

  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      if (editing) {
        await api.put(`/api/admin/caravanes/${id}`, { nom: form.nom, departLe: form.departLe })
      } else {
        await api.post('/api/admin/caravanes', { ...form, capacite: Number(form.capacite), prixDepartKaolack: form.prixDepartKaolack ? Number(form.prixDepartKaolack) : null, lienWaveDepartKaolack: form.lienWaveDepartKaolack.trim() || null, prixDepartDestination: Number(form.prixDepartDestination), prixKaolackDestination: form.prixKaolackDestination ? Number(form.prixKaolackDestination) : null, lienWaveKaolackDestination: form.lienWaveKaolackDestination.trim() || null })
      }
      navigate('/admin/caravanes')
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'La caravane n’a pas pu être enregistrée.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <p className="text-secondary">Chargement de la caravane…</p>

  return (
    <>
      <div className="ev-admin-page-head"><div><h1>{editing ? 'Modifier la caravane' : 'Ajouter une caravane'}</h1><p>{editing ? 'Modifiez son nom ou son horaire de départ.' : 'Configurez le trajet, les sièges, les tarifs et les liens Wave.'}</p></div><Link to="/admin/caravanes" className="btn btn-outline-secondary">Retour à la liste</Link></div>
      <form className="ev-admin-panel ev-admin-form-card" onSubmit={submit}>
        {error && <div className="alert alert-danger">{error}</div>}
        <div className="row g-3">
          <div className="col-md-6"><label className="form-label ev-admin-form-label">Nom de la caravane</label><input className="form-control" value={form.nom} onChange={setValue('nom')} maxLength="100" placeholder="Ex. Caravane de Tabaski" required /></div>
          <div className="col-md-6"><label className="form-label ev-admin-form-label">Date et heure de départ</label><input className="form-control" type="datetime-local" value={form.departLe} onChange={setValue('departLe')} required /></div>
          {!editing && <>
            <div className="col-md-4"><label className="form-label ev-admin-form-label">Ville de départ</label><select className="form-select" value={form.villeDepart} onChange={setValue('villeDepart')}><option>Dakar</option><option>Ziguinchor</option></select></div>
            <div className="col-md-4"><label className="form-label ev-admin-form-label">Ville d’arrivée</label><input className="form-control bg-light" value={villeArrivee} readOnly /></div>
            <div className="col-md-4"><label className="form-label ev-admin-form-label">Ville intermédiaire</label><input className="form-control bg-light" value="Kaolack" readOnly /></div>
            <div className="col-md-6"><label className="form-label ev-admin-form-label">Lieux de départ proposés aux passagers</label><div className="form-control bg-light d-flex align-items-center gap-2 flex-wrap h-auto">{(form.villeDepart === 'Dakar' ? ['Bountou Pikine', 'Sedima'] : ['Entre Pavillon']).map((point) => <span className="badge text-bg-light border" key={point}>{point}</span>)}</div></div>
            <div className="col-md-6"><label className="form-label ev-admin-form-label">Nombre de sièges (provisoire)</label><input className="form-control" type="number" min="1" max="100" value={form.capacite} onChange={setValue('capacite')} required /></div>
            <div className="col-12 mt-4"><h2 className="h6 fw-bold">Tarifs et liens de paiement Wave</h2><p className="text-secondary small">Les liens sont ceux fournis par le propriétaire. Le paiement devra être vérifié manuellement dans Wave.</p></div>
            <div className="col-md-6"><label className="form-label ev-admin-form-label">Départ → Kaolack · Prix (FCFA, facultatif)</label><input className="form-control mb-2" type="number" min="1" value={form.prixDepartKaolack} onChange={setValue('prixDepartKaolack')} /><input className="form-control" type="url" placeholder="Lien Wave (facultatif)" value={form.lienWaveDepartKaolack} onChange={setValue('lienWaveDepartKaolack')} /><small className="text-secondary">Laissez les deux champs vides si ce trajet n’est pas proposé.</small></div>
            <div className="col-md-6"><label className="form-label ev-admin-form-label">Départ → {villeArrivee} · Prix (FCFA)</label><input className="form-control mb-2" type="number" min="1" value={form.prixDepartDestination} onChange={setValue('prixDepartDestination')} required /><input className="form-control" type="url" placeholder="Lien Wave" value={form.lienWaveDepartDestination} onChange={setValue('lienWaveDepartDestination')} required /></div>
            <div className="col-md-6"><label className="form-label ev-admin-form-label">Kaolack → {villeArrivee} · Prix (FCFA, facultatif)</label><input className="form-control mb-2" type="number" min="1" value={form.prixKaolackDestination} onChange={setValue('prixKaolackDestination')} /><input className="form-control" type="url" placeholder="Lien Wave (facultatif)" value={form.lienWaveKaolackDestination} onChange={setValue('lienWaveKaolackDestination')} /><small className="text-secondary">Laissez ces deux champs vides si ce trajet n’est pas proposé.</small></div>
          </>}
        </div>
        {editing && <p className="alert alert-info mt-3 mb-0 small">Le trajet, les sièges et les tarifs existants ne changent pas.</p>}
        <div className="d-flex justify-content-end gap-2 mt-4"><Link to="/admin/caravanes" className="btn btn-outline-secondary">Annuler</Link><button className="btn btn-success" disabled={saving}>{saving ? 'Enregistrement…' : editing ? 'Enregistrer les modifications' : 'Créer la caravane'}</button></div>
      </form>
    </>
  )
}
