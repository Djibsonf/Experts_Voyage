import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../api/client'
import illustration from '../../assets/change_password.png'
import '../../styles/admin.css'

export default function AdminPasswordChange() {
  const [form, setForm] = useState({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' })
  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [visible, setVisible] = useState({ ancienMotDePasse: false, nouveauMotDePasse: false, confirmation: false })

  const change = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  const toggleVisible = (field) => setVisible((current) => ({ ...current, [field]: !current[field] }))
  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setNotice('')
    if (form.nouveauMotDePasse !== form.confirmation) {
      setError('Les deux nouveaux mots de passe ne correspondent pas.')
      return
    }
    setSaving(true)
    try {
      await api.put('/api/admin/profil/mot-de-passe', {
        ancienMotDePasse: form.ancienMotDePasse,
        nouveauMotDePasse: form.nouveauMotDePasse,
      })
      setForm({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' })
      setNotice('Votre mot de passe a été modifié.')
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'Modification impossible. Vérifiez votre mot de passe actuel.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="ev-auth-page ev-password-page min-vh-100 d-flex flex-column align-items-center justify-content-center p-3">
      <div className="ev-auth-card ev-password-card row g-0 bg-white rounded-4 overflow-hidden">
        <section className="ev-auth-visual col-md-6 p-4 d-none d-md-flex flex-column justify-content-center align-items-center gap-3">
          <img className="img-fluid" src={illustration} alt="Illustration de sécurisation du compte" />
          <p className="text-center text-secondary small mb-0">Protégez l’accès à votre espace administrateur.</p>
        </section>
        <section className="col-md-6 p-4 p-md-5 d-flex justify-content-center align-items-center">
          <div className="ev-auth-form">
            <div className="text-center mb-4">
              <h1 className="h4 fw-bold mb-1">Changer le mot de passe</h1>
              <p className="text-secondary small mb-0">Choisissez un nouveau mot de passe sécurisé.</p>
            </div>
            {error && <div className="alert alert-danger py-2 small" role="alert">{error}</div>}
            {notice && <div className="alert alert-success py-2 small" role="status">{notice}</div>}
            <form onSubmit={submit}>
              <label className="form-label fw-semibold small" htmlFor="old-password">Mot de passe actuel</label>
              <div className="input-group mb-3"><input className="form-control" id="old-password" type={visible.ancienMotDePasse ? 'text' : 'password'} name="ancienMotDePasse" autoComplete="current-password" value={form.ancienMotDePasse} onChange={change} required /><button className="btn btn-outline-secondary ev-password-toggle" type="button" onClick={() => toggleVisible('ancienMotDePasse')} aria-label={visible.ancienMotDePasse ? 'Masquer le mot de passe actuel' : 'Afficher le mot de passe actuel'}><i className={`bi ${visible.ancienMotDePasse ? 'bi-eye-slash' : 'bi-eye'}`} /></button></div>
              <label className="form-label fw-semibold small" htmlFor="new-password">Nouveau mot de passe</label>
              <div className="input-group mb-3"><input className="form-control" id="new-password" type={visible.nouveauMotDePasse ? 'text' : 'password'} name="nouveauMotDePasse" autoComplete="new-password" minLength="8" value={form.nouveauMotDePasse} onChange={change} required /><button className="btn btn-outline-secondary ev-password-toggle" type="button" onClick={() => toggleVisible('nouveauMotDePasse')} aria-label={visible.nouveauMotDePasse ? 'Masquer le nouveau mot de passe' : 'Afficher le nouveau mot de passe'}><i className={`bi ${visible.nouveauMotDePasse ? 'bi-eye-slash' : 'bi-eye'}`} /></button></div>
              <label className="form-label fw-semibold small" htmlFor="confirm-password">Confirmer le nouveau mot de passe</label>
              <div className="input-group"><input className="form-control" id="confirm-password" type={visible.confirmation ? 'text' : 'password'} name="confirmation" autoComplete="new-password" minLength="8" value={form.confirmation} onChange={change} required /><button className="btn btn-outline-secondary ev-password-toggle" type="button" onClick={() => toggleVisible('confirmation')} aria-label={visible.confirmation ? 'Masquer la confirmation' : 'Afficher la confirmation'}><i className={`bi ${visible.confirmation ? 'bi-eye-slash' : 'bi-eye'}`} /></button></div>
              <button className="btn ev-auth-submit w-100 mt-4" disabled={saving}>{saving ? 'Enregistrement…' : 'Enregistrer le nouveau mot de passe'}</button>
            </form>
            <div className="text-center mt-4"><Link className="ev-auth-link small" to="/admin/profil"><i className="bi bi-arrow-left me-2" aria-hidden="true" />Retour au profil</Link></div>
          </div>
        </section>
      </div>
    </div>
  )
}
