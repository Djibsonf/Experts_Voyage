import { useEffect, useState } from 'react'
import api from '../../api/client'

export default function AdminProfile() {
  const [profile, setProfile] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/api/admin/profil')
      .then(({ data }) => setProfile(data))
      .catch(() => setError('Impossible de charger le profil.'))
  }, [])

  return <>
    <div className="ev-admin-page-head"><div><h1>Mon profil</h1><p>Informations du compte administrateur.</p></div></div>
    {error && <div className="alert alert-danger">{error}</div>}
    <section className="ev-admin-profile-card">
      {profile ? <>
        <div className="ev-admin-profile-card-head">
          <span className="ev-admin-profile-large-avatar">{profile.nomComplet?.charAt(0) ?? 'A'}</span>
          <span className="ev-admin-role-badge"><i className="bi bi-shield-check me-1" />{profile.role}</span>
          <h2>{profile.nomComplet}</h2>
          <p>Administrateur Experts Voyages</p>
        </div>
        <div className="ev-admin-profile-details">
          <div><span><i className="bi bi-person me-2" />Nom complet</span><strong>{profile.nomComplet}</strong></div>
          <div><span><i className="bi bi-envelope me-2" />Adresse e-mail</span><strong>{profile.email}</strong></div>
        </div>
      </> : !error && <p className="text-secondary mb-0">Chargement…</p>}
    </section>
  </>
}
