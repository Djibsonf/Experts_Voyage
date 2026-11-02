import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../api/client'

export default function AdminDashboard() {
  const [stats, setStats] = useState({ caravanesEnCours: 0, passagers: 0 })
  const [caravanes, setCaravanes] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([api.get('/api/admin/tableau-de-bord'), api.get('/api/admin/caravanes')])
      .then(([statsResponse, caravansResponse]) => {
        setStats(statsResponse.data)
        setCaravanes(caravansResponse.data.slice(0, 5))
      })
      .catch(() => setError('Impossible de charger le tableau de bord.'))
  }, [])

  return (
    <>
      <div className="ev-admin-page-head"><div><h1>Tableau de bord</h1><p>Vue d’ensemble de vos caravanes et réservations.</p></div><Link className="btn btn-success" to="/admin/caravanes/nouvelle"><i className="bi bi-plus-lg me-2" />Ajouter une caravane</Link></div>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-xl-4"><div className="ev-admin-stat"><div><span>Caravanes en cours</span><strong>{stats.caravanesEnCours}</strong></div><i className="bi bi-bus-front" /></div></div>
        <div className="col-sm-6 col-xl-4"><div className="ev-admin-stat"><div><span>Passagers enregistrés</span><strong>{stats.passagers}</strong></div><i className="bi bi-people" /></div></div>
      </div>
      <section className="ev-admin-panel">
        <div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h5 mb-0">Prochaines caravanes</h2><Link to="/admin/caravanes" className="small text-success fw-semibold">Tout afficher</Link></div>
        {caravanes.length === 0 ? <div className="ev-admin-empty"><i className="bi bi-bus-front" />Aucune caravane enregistrée pour le moment.</div> : (
          <div className="table-responsive"><table className="table ev-admin-table align-middle mb-0"><thead><tr><th>Caravane</th><th>Trajet</th><th>Départ</th><th>Passagers</th><th /></tr></thead><tbody>
            {caravanes.map((item) => <tr key={item.id}><td className="fw-semibold">{item.nom}</td><td>{item.villeDepart} → {item.villeArrivee} via Kaolack</td><td>{new Date(item.departLe).toLocaleString('fr-FR', { dateStyle: 'medium', timeStyle: 'short' })}</td><td>{item.reservations}/{item.capacite}</td><td><Link className="btn btn-sm btn-outline-success" to={`/admin/caravanes/${item.id}`}>Voir</Link></td></tr>)}
          </tbody></table></div>
        )}
      </section>
    </>
  )
}
