import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import api from '../../api/client'

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
  const [caravanes, setCaravanes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

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

  const archive = async (item) => {
    if (!window.confirm(`Archiver « ${item.nom} » ?`)) return
    try { await api.patch(`/api/admin/caravanes/${item.id}/archiver`); await load() }
    catch (requestError) { setError(requestError.response?.data?.message ?? 'Archivage impossible.') }
  }
  const unarchive = async (item) => {
    try { await api.patch(`/api/admin/caravanes/${item.id}/desarchiver`); await load() }
    catch (requestError) { setError(requestError.response?.data?.message ?? 'Désarchivage impossible.') }
  }

  return <>
    <div className="ev-admin-page-head"><div><h1>{history ? 'Historique des caravanes' : 'Caravanes en cours'}</h1><p>{history ? 'Retrouvez les caravanes terminées ou archivées.' : 'Consultez, modifiez ou archivez les départs prévus.'}</p></div>{!history && <Link to="/admin/caravanes/nouvelle" className="btn btn-success"><i className="bi bi-plus-lg me-2" />Ajouter</Link>}</div>
    {error && <div className="alert alert-danger">{error}</div>}
    <section className="ev-admin-panel">{loading ? <p className="text-secondary mb-0">Chargement…</p> : <CaravanTable caravanes={caravanes} history={history} onArchive={archive} onUnarchive={unarchive} />}</section>
  </>
}
