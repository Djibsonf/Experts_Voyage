import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api, { apiBase } from '../api/client'

function formatDate(date) {
  if (!date) return null
  const parsed = new Date(`${date}T00:00:00`)
  return Number.isNaN(parsed.getTime()) ? date : new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'long' }).format(parsed)
}

export default function CaravanesEnCours() {
  const [caravanes, setCaravanes] = useState([])
  const [loading, setLoading] = useState(Boolean(apiBase))

  useEffect(() => {
    if (!apiBase) return
    let active = true
    api.get('/api/caravanes/en-cours', { timeout: 4000 })
      .then(({ data }) => { if (active) setCaravanes(Array.isArray(data) ? data : data.caravanes ?? []) })
      .catch(() => { if (active) setCaravanes([]) })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])

  return (
    <section className="section ev-current-caravanes" id="caravanes-en-cours" aria-labelledby="current-caravanes-title">
      <div className="container">
        <div className="ev-current-heading">
          <p className="ev-section-kicker">Départs disponibles</p>
          <h2 id="current-caravanes-title">Caravanes en cours</h2>
          <p>Consultez les prochaines caravanes et choisissez le trajet qui vous convient.</p>
        </div>
        {loading ? (
          <p className="ev-current-empty" role="status"><span className="spinner-border spinner-border-sm me-2" aria-hidden="true" />Chargement des caravanes…</p>
        ) : caravanes.length === 0 ? (
          <div className="ev-current-empty"><i className="bi bi-bus-front" aria-hidden="true" /><p>Aucune caravane en cours pour le moment.</p></div>
        ) : (
          <div className="row justify-content-center g-3">
            {caravanes.map((caravane) => (
              <div className="col-md-6 col-lg-4" key={caravane.id}>
                <Link className="ev-current-card-link" to={`/reserver/${caravane.id}`} aria-label={`Choisir un siège pour la caravane ${caravane.villeDepart} vers ${caravane.villeArrivee}`}>
                <article className="ev-current-card h-100">
                  <span className="ev-current-badge">Disponible</span>
                  <h3>{caravane.villeDepart} <i className="bi bi-arrow-right" aria-hidden="true" /> {caravane.villeArrivee}</h3>
                  {caravane.dateDepart && <p><i className="bi bi-calendar3" aria-hidden="true" /> {formatDate(caravane.dateDepart)}{caravane.heureDepart ? ` · ${caravane.heureDepart}` : ''}</p>}
                  {caravane.lieuDepart && <p><i className="bi bi-geo-alt" aria-hidden="true" /> Départ : {caravane.lieuDepart}</p>}
                  <span className="ev-current-card-action">Voir les sièges <i className="bi bi-arrow-right" aria-hidden="true" /></span>
                </article>
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
