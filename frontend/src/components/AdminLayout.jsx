import { useEffect, useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import logo from '../assets/logo-horizontal@2x.png'
import api from '../api/client'
import { logout } from '../api/admin'
import '../styles/admin-shell.css'

const navigation = [
  { to: '/admin/tableau-de-bord', label: 'Tableau de bord', icon: 'bi-grid-1x2' },
  { to: '/admin/caravanes/nouvelle', label: 'Ajouter une caravane', icon: 'bi-plus-circle' },
  { to: '/admin/caravanes', label: 'Caravanes en cours', icon: 'bi-bus-front' },
  { to: '/admin/historique', label: 'Historique', icon: 'bi-clock-history' },
]

export default function AdminLayout() {
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [profileMenu, setProfileMenu] = useState(false)
  const today = new Intl.DateTimeFormat('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }).format(new Date())

  useEffect(() => {
    api.get('/api/admin/profil').then(({ data }) => setProfile(data)).catch(() => {})
  }, [])

  const signOut = async () => {
    await logout().catch(() => {})
    navigate('/admin', { replace: true })
  }

  return (
    <div className="ev-admin-shell">
      <header className="ev-admin-topbar">
        <span className="ev-admin-topbar-title">Espace administration</span>
        <div className="ev-admin-topbar-actions">
          <div className="ev-admin-top-date"><i className="bi bi-calendar3" />{today}</div>
          <div className="ev-admin-profile-wrap">
            <button className="ev-admin-profile-button" onClick={() => setProfileMenu(!profileMenu)} aria-expanded={profileMenu}>
              <span className="ev-admin-profile-avatar">{profile?.nomComplet?.charAt(0) ?? 'A'}</span>
              <span className="ev-admin-profile-name">{profile?.nomComplet ?? 'Administrateur'}<small>Administrateur</small></span>
              <i className="bi bi-chevron-down" />
            </button>
            {profileMenu && <div className="ev-admin-profile-menu">
              <button onClick={() => { setProfileMenu(false); navigate('/admin/profil') }}><i className="bi bi-person" /> Voir le profil</button>
              <button onClick={() => { setProfileMenu(false); navigate('/admin/profil/mot-de-passe') }}><i className="bi bi-key" /> Changer le mot de passe</button>
              <button className="ev-admin-logout" onClick={signOut}><i className="bi bi-box-arrow-right" /> Déconnexion</button>
            </div>}
          </div>
        </div>
      </header>
      <div className="ev-admin-workspace">
        <aside className="ev-admin-sidebar">
          <div>
            <p className="ev-admin-nav-label">MENU PRINCIPAL</p>
            <nav className="ev-admin-nav">
              {navigation.map((item) => (
            <NavLink end={item.to === '/admin/caravanes'} className={({ isActive }) => `ev-admin-nav-link${isActive ? ' active' : ''}`} to={item.to} key={item.to}>
                  <i className={`bi ${item.icon}`} aria-hidden="true" /><span>{item.label}</span>
                </NavLink>
              ))}
            </nav>
          </div>
          <div className="ev-admin-sidebar-bottom">
            <NavLink className="ev-admin-brand" to="/admin/tableau-de-bord" aria-label="Accueil administration">
              <img src={logo} alt="Experts Voyages" />
            </NavLink>
          </div>
        </aside>
        <main className="ev-admin-main"><div className="ev-admin-content"><Outlet /></div></main>
      </div>
    </div>
  )
}
