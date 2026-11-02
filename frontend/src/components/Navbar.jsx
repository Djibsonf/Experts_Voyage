import { useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import logo from '../assets/logo-horizontal@2x.png'
import '../styles/navbar.css'

const links = [
  { to: '/', icon: 'house', label: 'Accueil', end: true },
  { to: '/#caravanes', icon: 'bus-front', label: 'Nos caravanes' },
  { to: '/#a-propos', icon: 'info-circle', label: 'À propos' },
  { to: '/#temoignages', icon: 'chat-quote', label: 'Témoignages' },
  { to: '/#contact', icon: 'envelope', label: 'Contact' },
]

export default function Navbar() {
  const [open, setOpen] = useState(false)
  const close = () => setOpen(false)

  return (
    <nav className="navbar navbar-expand-lg fixed-top ev-navbar" aria-label="Navigation principale">
      <div className="container-fluid px-3 px-xl-5">
        <Link className="navbar-brand ev-brand" to="/" onClick={close}>
          <img src={logo} alt="Experts Voyages" />
        </Link>
        <button className="navbar-toggler ev-toggler" type="button" onClick={() => setOpen(!open)} aria-label={open ? 'Fermer le menu' : 'Ouvrir le menu'} aria-expanded={open} aria-controls="ev-navigation">
          <i className={`bi ${open ? 'bi-x-lg' : 'bi-list'} fs-3 text-ev-green`} />
        </button>
        <div id="ev-navigation" className={`collapse navbar-collapse ${open ? 'show' : ''}`}>
          <ul className="navbar-nav mx-auto ev-links">
            {links.map(({ to, icon, label, end }) => (
              <li className="nav-item" key={label}>
                {to.includes('#') ? (
                  <Link className="nav-link" to={to} onClick={close}>
                    <i className={`bi bi-${icon}`} aria-hidden="true" />{label}
                  </Link>
                ) : (
                  <NavLink className="nav-link" to={to} end={end} onClick={close}>
                    {({ isActive }) => (
                      <><i className={`bi bi-${icon}${isActive ? '-fill' : ''}`} aria-hidden="true" />{label}</>
                    )}
                  </NavLink>
                )}
              </li>
            ))}
          </ul>
          <Link to="/#caravanes-en-cours" className="btn btn-ev ev-booking" onClick={close}>
            <i className="bi bi-calendar-check" aria-hidden="true" />Réserver ma place
          </Link>
        </div>
      </div>
    </nav>
  )
}
