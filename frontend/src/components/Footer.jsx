import { Link } from 'react-router-dom'
import logo from '../assets/logo-horizontal@2x.png'
import '../styles/footer.css'

const links = [
  { label: 'Accueil', to: '/' },
  { label: 'Nos caravanes', to: '/#caravanes' },
  { label: 'À propos', to: '/#a-propos' },
  { label: 'Témoignages', to: '/#temoignages' },
  { label: 'Contact', to: '/#contact' },
]

export default function Footer() {
  return (
    <footer className="ev-footer">
      <div className="container">
        <div className="row gy-4 align-items-start">
          <div className="col-lg-5">
            <Link to="/" className="ev-footer-brand" aria-label="Experts Voyages, accueil">
              <img src={logo} alt="Experts Voyages" height="48" />
            </Link>
            <p className="ev-footer-description">Des caravanes entre Ziguinchor, Kaolack et Dakar.</p>
          </div>
          <div className="col-6 col-lg-3">
            <h2>Navigation</h2>
            <ul className="ev-footer-links">
              {links.map(({ label, to }) => <li key={label}><Link to={to}>{label}</Link></li>)}
            </ul>
          </div>
          <div className="col-6 col-lg-4">
            <h2>Nous joindre</h2>
            <a href="tel:+221763628768">76 362 87 68 <span>(WhatsApp)</span></a>
            <a href="tel:+221780196587">78 019 65 87</a>
          </div>
        </div>
        <div className="ev-footer-bottom"><span>© {new Date().getFullYear()} Experts Voyages</span><span>Voyagez en toute sérénité.</span></div>
      </div>
    </footer>
  )
}
