import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import logo from '../assets/logo-horizontal@2x.png'
import loginIllustration from '../assets/login.png'
import forgotIllustration from '../assets/forgot_password.png'
import { login } from '../api/admin'
import '../styles/admin.css'

export default function AdminAuth() {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const forgot = pathname === '/admin/mot-de-passe-oublie'
  const [showPassword, setShowPassword] = useState(false)
  const [notice, setNotice] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    const robots = document.querySelector('meta[name="robots"]')
    const previousTitle = document.title
    const previousRobots = robots?.content
    document.title = forgot ? 'Mot de passe oublié | Experts Voyages' : 'Administration | Experts Voyages'
    if (robots) robots.content = 'noindex, nofollow'
    return () => {
      document.title = previousTitle
      if (robots && previousRobots) robots.content = previousRobots
    }
  }, [forgot])

  const submit = async (event) => {
    event.preventDefault()
    setNotice('')
    if (forgot) {
      setNotice('La récupération automatique n’est pas configurée. Contactez le responsable du site.')
      return
    }
    setSubmitting(true)
    try {
      await login(email, password)
      navigate('/admin/tableau-de-bord', { replace: true })
    } catch (error) {
      setNotice(error.response?.data?.message ?? 'Connexion impossible. Vérifiez le serveur et vos identifiants.')
    } finally {
      setSubmitting(false)
    }
  }

  const legal = (
    <div className="text-center text-secondary small">
      <div>Version 1.0.0</div>
      <div>© {new Date().getFullYear()} Experts Voyages - Tous droits réservés</div>
    </div>
  )

  return (
    <main className="ev-auth-page min-vh-100 d-flex flex-column align-items-center justify-content-center p-3">
      <div className="ev-auth-card row g-0 bg-white rounded-4 overflow-hidden">
        <section className="ev-auth-visual col-md-6 p-4 d-none d-md-flex flex-column justify-content-center align-items-center gap-3">
          <img className="img-fluid" src={forgot ? forgotIllustration : loginIllustration} alt="" />
          {legal}
        </section>

        <section className="col-md-6 p-4 p-md-5 d-flex justify-content-center align-items-center">
          <div className="ev-auth-form">
            <div className="text-center mb-4">
              <Link to="/" aria-label="Retour au site Experts Voyages">
                <img src={logo} alt="Experts Voyages" height="64" />
              </Link>
              <h1 className="h4 fw-bold mt-3 mb-1">Espace administrateur</h1>
              <p className="text-secondary small mb-0">
                {forgot
                  ? 'Saisissez votre email pour recevoir un nouveau mot de passe'
                  : 'Gestion des caravanes et des réservations'}
              </p>
            </div>

            <form onSubmit={submit}>
              <label className="form-label fw-semibold small" htmlFor="admin-email">Email</label>
              <input className="form-control mb-3" id="admin-email" type="email" autoComplete="username" placeholder="votre@email.com" value={email} onChange={(event) => setEmail(event.target.value)} required />

              {!forgot && (
                <>
                  <label className="form-label fw-semibold small" htmlFor="admin-password">Mot de passe</label>
                  <div className="input-group">
                    <input className="form-control" id="admin-password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" placeholder="Votre mot de passe" value={password} onChange={(event) => setPassword(event.target.value)} required />
                    <button className="btn btn-outline-secondary" type="button" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}>
                      <i className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`} aria-hidden="true" />
                    </button>
                  </div>
                  <div className="text-end mt-2 mb-3">
                    <Link className="ev-auth-link small" to="/admin/mot-de-passe-oublie">Mot de passe oublié ?</Link>
                  </div>
                </>
              )}

              {notice && <div className="alert alert-warning py-2 small" role="alert">{notice}</div>}
              <button className="btn ev-auth-submit w-100 mt-2" type="submit" disabled={submitting}>{submitting ? 'Connexion…' : forgot ? 'Envoyer' : 'Se connecter'}</button>
            </form>

            {forgot && (
              <div className="text-center mt-4">
                <Link className="ev-auth-link small" to="/admin">
                  <i className="bi bi-arrow-left me-2" aria-hidden="true" />Retour à la connexion
                </Link>
              </div>
            )}
          </div>
        </section>

      </div>
      <div className="d-md-none mt-3">{legal}</div>
    </main>
  )
}
