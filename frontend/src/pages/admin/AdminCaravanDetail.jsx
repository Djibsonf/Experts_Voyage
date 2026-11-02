import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../api/client'
import '../../styles/passenger-print.css'

export default function AdminCaravanDetail() {
  const { id } = useParams()
  const [caravane, setCaravane] = useState(null)
  const [passagers, setPassagers] = useState([])
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  useEffect(() => {
    Promise.all([api.get(`/api/admin/caravanes/${id}`), api.get(`/api/admin/caravanes/${id}/passagers`)])
      .then(([caravanResponse, passengersResponse]) => { setCaravane(caravanResponse.data); setPassagers(passengersResponse.data) })
      .catch((requestError) => setError(requestError.response?.data?.message ?? 'Impossible de charger les informations de la caravane.'))
  }, [id])

  const removeReservation = async (passager) => {
    if (!window.confirm(`Supprimer la réservation de ${passager.prenom} ${passager.nom} ? Cette action libérera le siège.`)) return
    try {
      await api.delete(`/api/admin/reservations/${passager.reservationId}`)
      setPassagers((current) => current.filter((item) => item.reservationId !== passager.reservationId))
      setNotice('La réservation non payée a été supprimée et le siège libéré.')
    } catch (requestError) { setError(requestError.response?.data?.message ?? 'Suppression impossible.') }
  }

  return <>
    <div className="ev-admin-page-head"><div><h1>{caravane?.nom ?? 'Passagers de la caravane'}</h1><p>{caravane ? `${caravane.villeDepart} → ${caravane.villeArrivee} · ${new Date(caravane.departLe).toLocaleString('fr-FR', { dateStyle: 'long', timeStyle: 'short' })}` : 'Détails et liste des passagers.'}</p></div><div className="ev-passenger-actions d-flex gap-2"><Link to="/admin/caravanes" className="btn btn-outline-secondary">Retour</Link>{caravane && <button type="button" onClick={() => window.print()} className="btn btn-success"><i className="bi bi-file-earmark-pdf me-2" />Exporter en PDF</button>}</div></div>
    {error && <div className="alert alert-danger">{error}</div>}
    {notice && <div className="alert alert-success">{notice}</div>}
    <section className="ev-admin-panel"><div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h5 mb-0">Liste des passagers</h2><span className="badge text-bg-success">{passagers.length} passager{passagers.length === 1 ? '' : 's'}</span></div>
      {!caravane && !error ? <p className="text-secondary">Chargement…</p> : passagers.length === 0 ? <div className="ev-admin-empty"><i className="bi bi-people" />Aucun passager enregistré pour cette caravane.</div> : (
        <div className="table-responsive"><table className="table ev-admin-table align-middle mb-0"><thead><tr><th>Siège</th><th>Passager</th><th>Pièce d’identité</th><th>Départ</th><th>Destination</th><th>Montant</th><th>Réservation</th><th /></tr></thead><tbody>
          {passagers.map((p) => <tr key={p.reservationId}><td><span className="badge text-bg-light border">{p.siege}</span></td><td className="fw-semibold">{p.prenom} {p.nom}</td><td><span className="d-block">{p.typeDocument === 'STUDENT_CARD' ? 'Carte étudiant' : 'CNI'}</span><small className="text-secondary">{p.numeroDocument}</small></td><td><span className="d-block">{p.villeDepart}</span><small className="text-secondary">{p.lieuDepart}</small></td><td>{p.villeArrivee}</td><td>{new Intl.NumberFormat('fr-FR').format(p.montantXof)} F&nbsp;CFA</td><td><span className="badge text-bg-warning">{p.statutPaiement}</span></td><td>{p.statutPaiement === 'EN_ATTENTE_PAIEMENT' && <button className="btn btn-sm btn-outline-danger" onClick={() => removeReservation(p)} title="Supprimer la réservation de test"><i className="bi bi-trash" /></button>}</td></tr>)}
        </tbody></table></div>
      )}
    </section>
  </>
}
