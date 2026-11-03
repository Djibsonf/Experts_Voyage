import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import api from '../../api/client'
import { useToast } from '../../components/ToastProvider'

function CaravanTable({ caravanes, history, onArchive, onUnarchive }) {
  if (!caravanes.length) return <div className="ev-admin-empty"><i className="bi bi-bus-front" />{history ? 'Aucune caravane dans l’historique.' : 'Aucune caravane en cours.'}</div>
  return <div className="table-responsive"><table className="table ev-admin-table align-middle mb-0"><thead><tr><th>Caravane</th><th>Trajet</th><th>Date et heure</th><th>Passagers</th><th>État</th><th className="text-end">Actions</th></tr></thead><tbody>
    {caravanes.map((item) => <tr key={item.id}>
      <td className="fw-semibold">{item.nom}</td><td>{item.villeDepart} → {item.villeArrivee}<small className="d-block text-secondary">via Kaolack</small></td>
      <td>{new Date(item.departLe).toLocaleString('fr-FR', { dateStyle: 'medium', timeStyle: 'short' })}</td>
      <td>{item.reservations} / {item.capacite}</td><td><span className={`badge ${item.archivee ? 'text-bg-secondary' : 'text-bg-success'}`}>{item.archivee ? 'Archivée' : item.statut}</span></td>
      <td><div className="d-flex justify-content-end gap-1"><Link className="btn btn-sm btn-outline-success" title="Voir les passagers" to={`/admin/caravanes/${item.id}`}><i className="bi bi-eye" /></Link><Link className="btn btn-sm btn-outline-secondary" title="Modifier" to={`/admin/caravanes/${item.id}/modifier`}><i className="bi bi-pencil" /></Link>{history ? <button className="btn btn-sm btn-outline-success" title="Désarchiver" onClick={() => onUnarchive(item)}><i className="bi bi-arrow-counterclockwise" /></button> : <button className="btn btn-sm btn-outline-danger" title="Archiver" onClick={() => onArchive(item)}><i className="bi bi-archive" /></button>}</div></td>
    </tr>)}
  </tbody></table></div>
}

export default function AdminCaravanList({ history = false }) {
  const location = useLocation()
  const showToast = useToast()
  const [caravanes, setCaravanes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [caravaneAArchiver, setCaravaneAArchiver] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const { data } = await api.get(history ? '/api/admin/caravanes/historique' : '/api/admin/caravanes')
      setCaravanes(data)
      setError('')
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'Impossible de charger les caravanes.')
    } finally {
      setLoading(false)
    }
  }, [history])

  useEffect(() => { load() }, [load, location.key])

  const archive = async () => {
    if (!caravaneAArchiver) return
    try { await api.patch(`/api/admin/caravanes/${caravaneAArchiver.id}/archiver`); showToast(`« ${caravaneAArchiver.nom} » a été archivée.`); setCaravaneAArchiver(null); await load() }
    catch (requestError) { showToast(requestError.response?.data?.message ?? 'Archivage impossible.', 'danger') }
  }
  const unarchive = async (item) => {
    try { await api.patch(`/api/admin/caravanes/${item.id}/desarchiver`); showToast(`« ${item.nom} » a été désarchivée.`); await load() }
    catch (requestError) { showToast(requestError.response?.data?.message ?? 'Désarchivage impossible.', 'danger') }
  }

  return <>
    <div className="ev-admin-page-head"><div><h1>{history ? 'Historique des caravanes' : 'Caravanes en cours'}</h1><p>{history ? 'Retrouvez les caravanes terminées ou archivées.' : 'Consultez, modifiez ou archivez les départs prévus.'}</p></div>{!history && <Link to="/admin/caravanes/nouvelle" className="btn btn-success"><i className="bi bi-plus-lg me-2" />Ajouter</Link>}</div>
    {error && <div className="alert alert-danger">{error}</div>}
    <section className="ev-admin-panel">{loading ? <p className="text-secondary mb-0">Chargement…</p> : <CaravanTable caravanes={caravanes} history={history} onArchive={setCaravaneAArchiver} onUnarchive={unarchive} />}</section>
    {caravaneAArchiver && <div className="ev-confirm-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setCaravaneAArchiver(null) }}>
      <section className="ev-confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="archive-dialog-title" aria-describedby="archive-dialog-description">
        <div className="ev-confirm-icon"><i className="bi bi-archive" /></div>
        <h2 id="archive-dialog-title">Archiver cette caravane ?</h2>
        <p id="archive-dialog-description">Voulez-vous archiver « {caravaneAArchiver.nom} » ? Elle ne sera plus proposée aux réservations.</p>
        <div className="d-flex justify-content-end gap-2"><button type="button" className="btn btn-outline-secondary" onClick={() => setCaravaneAArchiver(null)}>Annuler</button><button type="button" className="btn btn-danger" onClick={archive}><i className="bi bi-archive me-2" />Archiver</button></div>
      </section>
    </div>}
  </>
}
