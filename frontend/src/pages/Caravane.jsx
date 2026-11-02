const steps = [
  { number: '01', title: 'Cliquez sur « Réserver »', text: 'Accédez à la liste des caravanes proposées au départ.' },
  { number: '02', title: 'Choisissez votre caravane', text: 'Choisissez un trajet disponible, par exemple Dakar → Ziguinchor ou Ziguinchor → Dakar.' },
  { number: '03', title: 'Réservez votre place', text: 'Renseignez les informations nécessaires pour confirmer votre réservation.' },
  { number: '04', title: 'Payez avec Wave', text: 'Réglez votre réservation via Wave pour finaliser votre place.' },
]

export default function Caravane() {
  return (
    <section className="section ev-how" id="caravanes" aria-labelledby="caravanes-title">
      <div className="container">
        <div className="ev-caravan-intro">
          <p className="ev-section-kicker">Nos caravanes</p>
          <h2 id="caravanes-title" className="ev-section-title">De Ziguinchor à Dakar, et de Dakar à Ziguinchor</h2>
          <p className="ev-caravan-description">Experts Voyages organise des trajets dans les deux sens, avec passage par Kaolack.</p>
          <p className="ev-caravan-periods">Les caravanes sont organisées à l’ouverture et à la fermeture de l’université, ainsi qu’à l’occasion des fêtes de Tabaski, Korité, Noël et Pâques.</p>
        </div>
        <div className="ev-departure-points">
          <h3>Lieux de départ</h3>
          <div className="row g-3">
            <div className="col-md-6">
              <article className="ev-departure-card h-100">
                <span className="ev-departure-city"><i className="bi bi-geo-alt-fill" aria-hidden="true" /> Dakar</span>
                <ul><li>Bountou Pikine</li><li>Sedima</li></ul>
              </article>
            </div>
            <div className="col-md-6">
              <article className="ev-departure-card h-100">
                <span className="ev-departure-city"><i className="bi bi-geo-alt-fill" aria-hidden="true" /> Ziguinchor</span>
                <ul><li>Entrée Pavillon</li></ul>
              </article>
            </div>
          </div>
        </div>
        <div className="ev-how-divider" />
        <div className="row align-items-end g-3 mb-4 mb-lg-5">
          <div className="col-lg-8">
            <p className="ev-section-kicker">Comment ça marche</p>
            <h3 id="how-title" className="ev-section-title">Réservez votre place en 4 étapes</h3>
          </div>
          <p className="col-lg-4 ev-section-intro mb-0">Choisissez une caravane disponible, réservez votre siège et payez par Wave.</p>
        </div>
        <div className="row g-4">
          {steps.map(({ number, title, text }) => (
            <div className="col-md-6 col-xl-3" key={number}>
              <article className="ev-step h-100">
                <span className="ev-step-number">{number}</span>
                <h4>{title}</h4>
                <p>{text}</p>
              </article>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
