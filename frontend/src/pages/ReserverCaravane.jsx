import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../api/client'
import '../styles/reservation.css'

const initialPassenger = { firstName: '', lastName: '', documentType: 'STUDENT_CARD', documentNumber: '' }

function formatDeparture(value) {
  return value ? new Intl.DateTimeFormat('fr-FR', { dateStyle: 'full', timeStyle: 'short' }).format(new Date(value)) : ''
}

export default function ReserverCaravane() {
  const { id } = useParams()
  const [caravane, setCaravane] = useState(null)
  const [destinations, setDestinations] = useState([])
  const [points, setPoints] = useState([])
  const [seats, setSeats] = useState([])
  const [destinationId, setDestinationId] = useState('')
  const [pointId, setPointId] = useState('')
  const [selectedSeat, setSelectedSeat] = useState(null)
  const [passenger, setPassenger] = useState(initialPassenger)
  const [recoveryDetails, setRecoveryDetails] = useState({ lastName: '', documentNumber: '' })
  const [reservation, setReservation] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [cancelling, setCancelling] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    setLoading(true)
    Promise.all([
      api.get(`/api/caravanes/${id}`),
    ]).then(async ([{ data }]) => {
      if (!active) return
      setCaravane(data)
      const [pointResponse, destinationResponse] = await Promise.all([
        api.get(`/api/caravanes/${id}/points-de-depart`, { params: { originStopId: data.originStopId } }),
        api.get(`/api/caravanes/${id}/destinations`, { params: { originStopId: data.originStopId } }),
      ])
      if (!active) return
      setPoints(pointResponse.data)
      setPointId(String(pointResponse.data[0]?.id ?? ''))
      setDestinations(destinationResponse.data)
      setDestinationId(String(destinationResponse.data[0]?.stopId ?? ''))
      setError('')
    }).catch((requestError) => {
      if (active) setError(requestError.response?.data?.message ?? 'Impossible de charger cette caravane.')
    }).finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [id])

  useEffect(() => {
    if (destinations.length === 0) return
    const reservationId = window.localStorage.getItem(`ev-pending-reservation-${id}`)
    if (!reservationId) return
    let active = true
    api.get(`/api/reservations/${reservationId}`)
      .then(({ data }) => {
        if (!active) return
        setDestinationId(String(data.destinationStopId))
        setReservation(data)
      })
      .catch(() => window.localStorage.removeItem(`ev-pending-reservation-${id}`))
    return () => { active = false }
  }, [destinations, id])

  const loadSeats = useCallback(async () => {
    if (!caravane || !destinationId) return
    try {
      const { data } = await api.get(`/api/caravanes/${id}/sieges`, {
        params: { originStopId: caravane.originStopId, destinationStopId: destinationId },
      })
      setSeats(data)
      setSelectedSeat((current) => data.some((seat) => seat.numero === current && seat.statut === 'DISPONIBLE') ? current : null)
      setError('')
    } catch (requestError) {
      setSeats([])
      setSelectedSeat(null)
      setError(requestError.response?.data?.message ?? 'Impossible de charger les sièges.')
    }
  }, [caravane, destinationId, id])

  useEffect(() => { loadSeats() }, [loadSeats])

  const selectedDestination = useMemo(
    () => destinations.find((destination) => String(destination.stopId) === destinationId),
    [destinations, destinationId],
  )
  const seatByNumber = useMemo(() => new Map(seats.map((seat) => [seat.numero, seat])), [seats])
  const standardRows = useMemo(() => {
    const capacity = caravane?.capacite ?? 0
    const lastStandard = capacity === 55 ? 48 : Math.floor(capacity / 4) * 4
    const rows = []
    for (let number = 1; number <= lastStandard; number += 4) rows.push([number + 2, number + 3, number, number + 1])
    if (caravane?.capacite === 55) rows.push([49])
    return rows
  }, [caravane])
  const rearSeats = useMemo(() => {
    if (!caravane) return []
    const start = caravane.capacite === 55 ? 50 : Math.floor(caravane.capacite / 4) * 4 + 1
    return Array.from({ length: Math.min(6, caravane.capacite - start + 1) }, (_, index) => start + index)
  }, [caravane])

  const reserve = async (event) => {
    event.preventDefault()
    if (!selectedSeat) return
    setSaving(true)
    setError('')
    try {
      const { data } = await api.post('/api/reservations', {
        caravanId: Number(id), seatNumber: selectedSeat, originStopId: caravane.originStopId,
        destinationStopId: Number(destinationId), departurePointId: Number(pointId), ...passenger,
      })
      setReservation(data)
      window.localStorage.setItem(`ev-pending-reservation-${id}`, data.reservationId)
      setSelectedSeat(null)
      await loadSeats()
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'Ce siège n’a pas pu être réservé. Actualisez la carte et réessayez.')
      await loadSeats()
    } finally { setSaving(false) }
  }

  const recoverReservation = async (event) => {
    event.preventDefault()
    setError('')
    try {
      const { data } = await api.post('/api/reservations/reprendre', {
        caravanId: Number(id), ...recoveryDetails,
      })
      setDestinationId(String(data.destinationStopId))
      window.localStorage.setItem(`ev-pending-reservation-${id}`, data.reservationId)
      setReservation(data)
      setSelectedSeat(null)
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'Impossible de retrouver cette réservation.')
    }
  }

  const cancelReservation = async () => {
    if (!reservation || cancelling) return
    setCancelling(true)
    setError('')
    try {
      await api.post(`/api/reservations/${reservation.reservationId}/annuler`)
      window.localStorage.removeItem(`ev-pending-reservation-${id}`)
      setReservation(null)
      await loadSeats()
    } catch (requestError) {
      setError(requestError.response?.data?.message ?? 'Cette réservation ne peut plus être annulée.')
    } finally { setCancelling(false) }
  }

  const renderSeat = (number) => {
    const seat = seatByNumber.get(number)
    const unavailable = !seat || seat.statut !== 'DISPONIBLE'
    const selected = selectedSeat === number
    return <button key={number} type="button" className={`ev-seat${unavailable ? ' unavailable' : ''}${selected ? ' selected' : ''}`} disabled={unavailable} onClick={() => { setSelectedSeat(number); setReservation(null) }} aria-label={`Siège ${number}, ${unavailable ? 'réservé' : selected ? 'sélectionné' : 'disponible'}`} aria-pressed={selected}>{number}</button>
  }

  if (loading) return <section className="ev-booking-page"><div className="container py-5"><p className="text-secondary">Chargement de la caravane…</p></div></section>
  if (!caravane) return <section className="ev-booking-page"><div className="container py-5"><div className="alert alert-warning">{error || 'Cette caravane n’est plus disponible.'}</div><Link className="btn btn-outline-success" to="/#caravanes-en-cours">Retour aux caravanes</Link></div></section>

  return (
    <section className="ev-booking-page section">
      <div className="container">
        <Link className="ev-booking-back" to="/#caravanes-en-cours"><i className="bi bi-arrow-left" /> Toutes les caravanes</Link>
        <header className="ev-booking-heading"><p className="ev-section-kicker">Réservation de siège</p><h1>{caravane.nom}</h1><p>{caravane.villeDepart} <i className="bi bi-arrow-right" /> {caravane.villeIntermediaire ? <>{caravane.villeIntermediaire} <i className="bi bi-arrow-right" /></> : null} {caravane.villeArrivee} <span>· {formatDeparture(caravane.departLe)}</span></p></header>
        <div className="row g-4 align-items-start">
          <div className="col-lg-7">
            <div className="ev-booking-panel">
              <div className="ev-seat-map-heading"><div><h2>Choisissez votre siège</h2><p>Les places rouges sont occupées. Cliquez sur une place noire pour la sélectionner.</p></div><button className="btn btn-sm btn-outline-success" type="button" onClick={loadSeats}><i className="bi bi-arrow-clockwise me-1" />Actualiser</button></div>
              {error && <div className="alert alert-warning py-2" role="alert">{error}</div>}
              <div className="ev-seat-legend"><span><i className="ev-legend-seat free" />Libre</span><span><i className="ev-legend-seat taken" />Réservé</span><span><i className="ev-legend-seat chosen" />Votre choix</span></div>
              <div className="ev-bus-map"><div className="ev-bus-front"><i className="bi bi-steering-wheel" /> AVANT DU BUS</div>
                <div className="ev-seat-rows">{standardRows.map((row, index) => <div className={`ev-seat-row${row.length === 1 ? ' single' : ''}`} key={`row-${index}`}>
                  {row.length === 1 ? <>{renderSeat(row[0])}</> : <>{renderSeat(row[0])}{renderSeat(row[1])}<span className="ev-seat-aisle" />{renderSeat(row[2])}{renderSeat(row[3])}</>}
                </div>)}
                {rearSeats.length > 0 && <div className="ev-rear-row">{rearSeats.map(renderSeat)}</div>}</div>
              </div>
            </div>
          </div>
          <div className="col-lg-5">
            <div className="ev-booking-panel ev-booking-details">
              <h2>Votre trajet</h2>
              <label className="form-label">Destination</label>
              {reservation
                ? <div className="form-control mb-3 bg-light">{selectedDestination?.ville} · {new Intl.NumberFormat('fr-FR').format(reservation.amountXof)} F CFA</div>
                : <select className="form-select mb-3" value={destinationId} onChange={(event) => { setDestinationId(event.target.value); setReservation(null) }}>
                    {destinations.map((destination) => <option key={destination.stopId} value={destination.stopId}>{destination.ville} · {new Intl.NumberFormat('fr-FR').format(destination.prixXof)} F CFA</option>)}
                  </select>}
              <label className="form-label">Lieu de départ</label>
              <select className="form-select mb-3" value={pointId} onChange={(event) => setPointId(event.target.value)}>
                {points.map((point) => <option key={point.id} value={point.id}>{point.nom}</option>)}
              </select>
              {selectedSeat && <form onSubmit={reserve} className="ev-passenger-form">
                <div className="ev-selected-seat-note"><i className="bi bi-check-circle-fill" /> Siège {selectedSeat} sélectionné</div>
                <h3>Informations du passager</h3>
                <label className="form-label" htmlFor="passenger-first-name">Prénom</label><input id="passenger-first-name" className="form-control mb-3" value={passenger.firstName} onChange={(event) => setPassenger({ ...passenger, firstName: event.target.value })} maxLength="100" required />
                <label className="form-label" htmlFor="passenger-last-name">Nom</label><input id="passenger-last-name" className="form-control mb-3" value={passenger.lastName} onChange={(event) => setPassenger({ ...passenger, lastName: event.target.value })} maxLength="100" required />
                <label className="form-label" htmlFor="passenger-document-type">Document</label><select id="passenger-document-type" className="form-select mb-3" value={passenger.documentType} onChange={(event) => setPassenger({ ...passenger, documentType: event.target.value })}><option value="STUDENT_CARD">Carte étudiante</option><option value="NATIONAL_ID">Carte nationale d’identité</option></select>
                <label className="form-label" htmlFor="passenger-document-number">Numéro du document</label><input id="passenger-document-number" className="form-control" value={passenger.documentNumber} onChange={(event) => setPassenger({ ...passenger, documentNumber: event.target.value })} minLength="4" maxLength="80" pattern="[A-Za-z0-9 .\-/]+" required />
                <button className="btn btn-success w-100 mt-4" disabled={saving || !pointId}>{saving ? 'Réservation…' : 'Réserver et payer sur Wave'}</button>
              </form>}
              {!selectedSeat && !reservation && <><p className="ev-booking-hint"><i className="bi bi-info-circle" /> Sélectionnez une place noire pour continuer.</p><form className="ev-recover-form" onSubmit={recoverReservation}><h3>Vous aviez déjà réservé ?</h3><p>Retrouvez votre paiement avec le nom et le numéro de pièce saisis lors de la réservation.</p><label className="form-label" htmlFor="recover-last-name">Nom du passager</label><input id="recover-last-name" className="form-control mb-2" value={recoveryDetails.lastName} onChange={(event) => setRecoveryDetails({ ...recoveryDetails, lastName: event.target.value })} required /><label className="form-label" htmlFor="recover-document">Numéro de la pièce</label><input id="recover-document" className="form-control mb-2" value={recoveryDetails.documentNumber} onChange={(event) => setRecoveryDetails({ ...recoveryDetails, documentNumber: event.target.value })} required /><button className="btn btn-outline-success w-100" type="submit">Retrouver ma réservation</button></form></>}
              {reservation && <div className="ev-payment-confirmation"><i className="bi bi-check-circle-fill" /><h3>Place retenue</h3><p>Réservation <strong>{reservation.reservationCode}</strong>, siège {reservation.seatNumber}. La place est retenue jusqu’à {new Intl.DateTimeFormat('fr-FR', { timeStyle: 'short' }).format(new Date(reservation.holdExpiresAt))}.</p><a className="btn btn-success w-100" href={reservation.wavePaymentUrl} target="_blank" rel="noreferrer"><i className="bi bi-wallet2 me-2" />Payer {new Intl.NumberFormat('fr-FR').format(reservation.amountXof)} F CFA sur Wave</a><button className="btn btn-outline-danger w-100 mt-2" type="button" onClick={cancelReservation} disabled={cancelling}>{cancelling ? 'Annulation…' : 'Annuler la réservation et libérer le siège'}</button></div>}
              {selectedDestination && <p className="ev-booking-price">Prix du trajet <strong>{new Intl.NumberFormat('fr-FR').format(selectedDestination.prixXof)} F CFA</strong></p>}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
