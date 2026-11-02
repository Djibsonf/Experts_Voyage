const whatsappUrl = 'https://wa.me/221763628768?text=Bonjour%20Experts%20Voyages%2C%20je%20souhaite%20des%20informations%20sur%20les%20caravanes.'

export default function Contact() {
  return (
    <section className="section ev-contact" id="contact" aria-labelledby="contact-title">
      <div className="container">
        <div className="ev-contact-heading">
          <p className="ev-section-kicker">Contact</p>
          <h2 id="contact-title">Une question sur votre prochain trajet ?</h2>
          <p>Appelez Experts Voyages ou écrivez-nous sur WhatsApp.</p>
        </div>
        <div className="row justify-content-center g-3">
          <div className="col-md-5 col-lg-4">
            <article className="ev-contact-card h-100">
              <span className="ev-contact-icon ev-whatsapp-icon"><i className="bi bi-whatsapp" aria-hidden="true" /></span>
              <p className="ev-contact-label">Téléphone et WhatsApp</p>
              <a className="ev-contact-number" href="tel:+221763628768">76 362 87 68</a>
              <a className="btn ev-contact-action ev-contact-whatsapp" href={whatsappUrl} target="_blank" rel="noreferrer"><i className="bi bi-whatsapp" aria-hidden="true" /> Écrire sur WhatsApp</a>
            </article>
          </div>
          <div className="col-md-5 col-lg-4">
            <article className="ev-contact-card h-100">
              <span className="ev-contact-icon"><i className="bi bi-telephone" aria-hidden="true" /></span>
              <p className="ev-contact-label">Téléphone</p>
              <a className="ev-contact-number" href="tel:+221780196587">78 019 65 87</a>
              <a className="ev-contact-action ev-contact-call" href="tel:+221780196587">Appeler <i className="bi bi-arrow-right" aria-hidden="true" /></a>
            </article>
          </div>
        </div>
      </div>
    </section>
  )
}
