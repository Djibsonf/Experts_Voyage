import { Link } from 'react-router-dom'
import logo from '../assets/logo.png'
import '../styles/a-propos.css'

const commitments = [
  { icon: 'bus-front', title: 'Des bus climatisés', text: 'Voyagez à bord de bus climatisés sur les trajets organisés par Experts Voyages.' },
  { icon: 'luggage', title: 'Bagages transportés gratuitement', text: 'Le transport de vos bagages est gratuit pendant votre voyage.' },
  { icon: 'calendar2-week', title: 'Des départs au rythme de l’université', text: 'Les caravanes sont organisées autour des périodes d’ouverture et de fermeture de l’université et des fêtes.' },
]

export default function APropos() {
  return (
    <div className="ev-about">
      <section className="ev-about-hero" id="a-propos" aria-labelledby="about-title">
        <div className="container">
          <div className="row align-items-center gy-4">
            <div className="col-lg-7">
              <p className="ev-about-kicker">Notre compagnie</p>
              <h1 id="about-title">Experts Voyages, pour rapprocher les villes et les étudiants.</h1>
              <p className="ev-about-lead">Experts Voyages organise des caravanes entre Ziguinchor, Kaolack et Dakar, notamment pour accompagner les déplacements des étudiants de l’Université Assane Seck.</p>
              <Link className="btn btn-ev mt-3" to="/#caravanes">Découvrir nos caravanes <i className="bi bi-arrow-right ms-2" aria-hidden="true" /></Link>
            </div>
            <div className="col-lg-5 text-center">
              <img className="ev-about-logo" src={logo} alt="Logo Experts Voyages" width="280" />
            </div>
          </div>
        </div>
      </section>

      <section className="section ev-about-commitments" aria-labelledby="commitments-title">
        <div className="container">
          <div className="text-center ev-about-heading">
            <p className="ev-about-kicker">Notre engagement</p>
            <h2 id="commitments-title">Un voyage organisé autour de vos besoins</h2>
          </div>
          <div className="row g-3 g-lg-4">
            {commitments.map(({ icon, title, text }) => (
              <div className="col-md-4" key={title}>
                <article className="ev-about-card h-100">
                  <span className="ev-about-icon"><i className={`bi bi-${icon}`} aria-hidden="true" /></span>
                  <h3>{title}</h3>
                  <p>{text}</p>
                </article>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}
