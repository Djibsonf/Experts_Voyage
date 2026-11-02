const testimonials = [
  { name: 'Fallou Ngom', text: 'Je suis très content de ce voyage. La climatisation et le confort étaient largement suffisants.' },
  { name: 'Khady Tine', text: 'Merci du fond du cœur pour cette magnifique caravane ! Ton organisation, ta disponibilité et ton incroyable sens du détail ont rendu cette aventure inoubliable. Grâce à toi, tout s’est déroulé avec une grande facilité : les étapes, les activités et même les petits imprévus ont été gérés avec calme et bienveillance.' },
  { name: 'Daouda Faye', text: 'Merci infiniment pour ta générosité et ton altruisme.' },
]

export default function Temoignage() {
  return (
    <section className="section ev-testimonials" id="temoignages" aria-labelledby="testimonials-title">
      <div className="container">
        <div className="ev-testimonials-heading">
          <p className="ev-section-kicker">Témoignages</p>
          <h2 id="testimonials-title">Leurs expériences de voyage</h2>
        </div>
        <div className="row g-3 g-lg-4 align-items-stretch">
          <div className="col-lg-7">
            <figure className="ev-testimonial-card ev-testimonial-featured h-100">
              <i className="bi bi-quote ev-quote-mark" aria-hidden="true" />
              <blockquote>{testimonials[1].text}</blockquote>
              <figcaption><span className="ev-testimonial-avatar">{testimonials[1].name.charAt(0)}</span><span>{testimonials[1].name}</span></figcaption>
            </figure>
          </div>
          <div className="col-lg-5">
            <div className="ev-testimonial-stack h-100">
              {[testimonials[0], testimonials[2]].map(({ name, text }) => (
                <figure className="ev-testimonial-card ev-testimonial-compact" key={name}>
                  <i className="bi bi-quote ev-quote-mark" aria-hidden="true" />
                  <blockquote>{text}</blockquote>
                  <figcaption><span className="ev-testimonial-avatar">{name.charAt(0)}</span><span>{name}</span></figcaption>
                </figure>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
