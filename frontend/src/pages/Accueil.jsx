import heroImage from '../assets/hero.webp'
import directorPhoto from '../assets/pdg.jpeg'
import APropos from './APropos'
import Caravane from './Caravane'
import CaravanesEnCours from './CaravanesEnCours'
import Contact from './Contact'
import Temoignage from './Temoignage'
import '../styles/accueil.css'

export default function Accueil() {
  return (
    <div className="ev-home">
      <section className="ev-hero" aria-labelledby="hero-title">
        <img className="ev-hero-image" src={heroImage} alt="Un autocar Experts Voyages et des voyageurs prêts au départ" fetchPriority="high" />
        <div className="ev-hero-shade" />
        <div className="container ev-hero-content">
          <h1 id="hero-title">Des caravanes entre Dakar, Kaolack et Ziguinchor</h1>
          <p className="ev-hero-copy">Experts Voyages organise des caravanes entre <span className="ev-city">Ziguinchor</span>, <span className="ev-city">Kaolack</span> et <span className="ev-city">Dakar</span>, pour rapprocher les étudiants de leur destination.</p>
          <div className="d-flex flex-column flex-sm-row gap-3 mt-4">
            <a className="btn btn-ev ev-hero-primary" href="#caravanes-en-cours"><i className="bi bi-calendar-check" aria-hidden="true" />Réserver ma place</a>
            <a className="btn ev-hero-secondary" href="#caravanes">Voir les étapes <i className="bi bi-arrow-down-right" aria-hidden="true" /></a>
          </div>
        </div>
      </section>

      <section className="ev-director" aria-labelledby="director-title">
        <div className="container">
          <div className="ev-director-card">
            <img src={directorPhoto} alt="Portrait du PDG d’Experts Voyages" loading="lazy" />
            <div className="ev-director-message">
              <p className="ev-section-kicker">Le mot du PDG</p>
              <h2 id="director-title">Voyagez en toute sérénité</h2>
              <blockquote>« Avec Experts Voyages, voyagez en toute sérénité à bord de bus climatisés et profitez du transport gratuit de vos bagages ! »</blockquote>
              <span className="ev-director-signature">Mouhamed Ngom, PDG Experts Voyages</span>
            </div>
          </div>
        </div>
      </section>

      <CaravanesEnCours />
      <Caravane />
      <APropos />
      <Temoignage />
      <Contact />

    </div>
  )
}
